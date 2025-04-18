# Order Management Service

A microservice for managing food delivery orders, built with Spring Boot and Kotlin.

## Features

- Complete order lifecycle management
- State machine-based status transitions
- Event-driven architecture with RabbitMQ
- Service discovery with Eureka
- SLA enforcement for order states
- RESTful API endpoints
- MySQL persistence

## Prerequisites

- JDK 21
- MySQL 8.0+
- RabbitMQ 3.8+
- Eureka Server (optional)

## Configuration

The application can be configured via environment variables:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_order
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka
```

## Building

```bash
./gradlew clean build
```

## Running

```bash
./gradlew bootRun
```

## API Endpoints

### Orders

- `POST /api/v1/orders` - Create a new order
- `GET /api/v1/orders/{id}` - Get order by ID
- `GET /api/v1/orders/customer/{userId}` - Get customer's orders
- `GET /api/v1/orders/merchant/{restaurantId}/queue` - Get merchant's order queue
- `PATCH /api/v1/orders/{id}/cancel` - Cancel an order
- `PATCH /api/v1/orders/{id}/status` - Update order status

### Order Status Flow

```
NEW -> CONFIRMED -> PREPARING -> READY_FOR_PICKUP -> PICKED_UP -> DELIVERED
  |        |           |              |               |             |
  |        |           |              |               |             |
  v        v           v              v               v             v
REJECTED   CANCELLED   EXPIRED     EXPIRED      FAILED_DELIVERY -> RETURNED_TO_STORE
```

## Events

The service publishes events to RabbitMQ for order status changes:

- Exchange: `order.events`
- Routing key: `order.<status>`

## SLA Timers

- NEW → CONFIRMED: 5 minutes
- CONFIRMED → PREPARING: 3 minutes
- PREPARING → READY: 30 minutes
- READY → PICKED_UP: 10 minutes
- PICKED_UP → DELIVERED: 45 minutes 