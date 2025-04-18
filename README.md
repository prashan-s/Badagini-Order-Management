# Order Management Service

A robust microservice for managing food delivery orders, built with Spring Boot and Kotlin. This service handles the complete lifecycle of food delivery orders from creation to delivery, with built-in SLA monitoring and event-driven architecture.

## Features

- Complete order lifecycle management with state transitions
- Event-driven architecture using RabbitMQ for real-time updates
- Service discovery and registration with Netflix Eureka
- Automated SLA monitoring and enforcement
- RESTful API endpoints with comprehensive documentation
- Persistent storage with MySQL
- Fault-tolerant design with retry mechanisms
- Comprehensive logging and monitoring
- Spring State Machine integration for order flow management
- Payment service integration via event-driven architecture

## Prerequisites

- JDK 21 or higher
- MySQL 8.0+
- RabbitMQ 3.8+
- Eureka Server (optional for service discovery)
- Gradle 8.0+ (optional, wrapper included)

## Quick Start

1. Clone the repository:
```bash
git clone https://github.com/yourusername/order-management.git
cd order-management
```

2. Configure your environment variables (create a `.env` file):
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_order
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
RABBITMQ_HOST=app.dulanga.com
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VIRTUAL_HOST=badagini-host
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://app.dulanga.com/service-registry/eureka/
```

3. Build the project:
```bash
./gradlew clean build
```

4. Run the application:
```bash
./gradlew bootRun
```

## API Documentation

### Orders API

#### Create Order
- **Endpoint:** `POST /api/v1/orders`
- **Description:** Create a new food delivery order
- **Request Body:**
```json
{
    "customerUserId": "string",
    "restaurantId": "string",
    "items": [
        {
            "menuItemId": "string",
            "itemName": "string",
            "quantity": integer,
            "itemPrice": number
        }
    ],
    "deliveryAddress": {
        "street": "string",
        "city": "string",
        "state": "string",
        "zipCode": "string"
    }
}
```
- **Response:** `201 Created` with order details and payment information
```json
{
    "order": {
        "orderId": "string",
        "customerUserId": "string",
        "restaurantId": "string",
        "status": "NEW",
        "totalAmount": number,
        "deliveryAddress": "string",
        "orderTime": "string",
        "deliveryTime": null,
        "items": [],
        "statusHistory": []
    },
    "paymentRequired": true,
    "totalAmount": number
}
```

#### Get Order
- **Endpoint:** `GET /api/v1/orders/{id}`
- **Description:** Retrieve order details by ID
- **Response:** `200 OK` with order details

#### List Customer Orders
- **Endpoint:** `GET /api/v1/orders/customer/{userId}`
- **Description:** Get all orders for a specific customer
- **Parameters:**
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 20)
- **Response:** `200 OK` with paginated order list

#### Get Merchant Queue
- **Endpoint:** `GET /api/v1/orders/merchant/{restaurantId}/queue`
- **Description:** Get active orders queue for a restaurant
- **Parameters:**
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 20)
- **Response:** `200 OK` with queue details

#### Cancel Order
- **Endpoint:** `PATCH /api/v1/orders/{id}/cancel`
- **Parameters:**
  - `actor` (required): Who initiated the cancellation (CUSTOMER, MERCHANT, SYSTEM)
- **Description:** Cancel an existing order
- **Response:** `200 OK` with updated order status

#### Update Order Status
- **Endpoint:** `PATCH /api/v1/orders/{id}/status`
- **Parameters:**
  - `status` (required): New status for the order
  - `actor` (required): Who initiated the status change
- **Description:** Update the status of an order
- **Response:** `200 OK` with updated order details

### Order Status Flow

```
NEW -> CONFIRMED -> PREPARING -> READY_FOR_PICKUP -> PICKED_UP -> DELIVERED
  |        |           |              |               |             |
  |        |           |              |               |             |
  v        v           v              v               v             v
REJECTED   CANCELLED   EXPIRED     EXPIRED      FAILED_DELIVERY -> RETURNED_TO_STORE
```

## Event System

The service uses RabbitMQ for event-driven communication:

### Published Events
- Exchange: `order.events`
- Type: `topic`
- Events:
  - `order.created`: New order created
  - `order.updated`: Order status updated
  - `order.cancelled`: Order cancelled
  - `order.delivered`: Order successfully delivered
  - `order.failed`: Delivery failed

### Event Format
```json
{
    "orderId": "string",
    "status": "string",
    "timestamp": "ISO-8601 datetime",
    "source": "string"
}
```

### Payment Integration
- Exchange: `payment.events`
- Events consumed:
  - `payment.authorized`: Triggers order confirmation
  - `payment.failed`: Triggers order rejection

## Error Handling

The service includes comprehensive error handling for various scenarios:

- `404 Not Found`: Order, merchant, customer, or rider not found
- `400 Bad Request`: Invalid order state transition
- `402 Payment Required`: Payment processing failed
- `408 Request Timeout`: SLA breach detected
- `503 Service Unavailable`: Database or connection issues

## SLA Monitoring

The service enforces the following SLA timers for order state transitions:

| Transition | Time Limit | Action on Timeout |
|------------|------------|-------------------|
| NEW → CONFIRMED | 5 minutes | Auto-reject |
| CONFIRMED → PREPARING | 3 minutes | Notification |
| PREPARING → READY | 30 minutes | Notification |
| READY → PICKED_UP | 10 minutes | Notification |
| PICKED_UP → DELIVERED | 45 minutes | Mark for review |

## Development

### Running Tests
```bash
./gradlew test # Run unit tests
./gradlew integrationTest # Run integration tests
```

### Code Style
The project uses ktlint for Kotlin code formatting. Run:
```bash
./gradlew ktlintCheck # Check code style
./gradlew ktlintFormat # Format code
```

### Database Migrations
```bash
./gradlew flywayMigrate # Apply database migrations
./gradlew flywayClean # Reset database (use with caution)
```

## Monitoring and Logging

- Actuator endpoints enabled at `/actuator/*`
- Prometheus metrics at `/actuator/prometheus`
- Structured logging with JSON format
- Distributed tracing with Spring Cloud Sleuth

## Security

The service implements Spring Security with the following configuration:
- Public endpoints:
  - Swagger UI: `/swagger-ui/**`
  - OpenAPI docs: `/v3/api-docs/**`
  - Actuator: `/actuator/**`
  - Authentication: `/auth/login`, `/auth/register`, `/auth/verify`
- All other endpoints require authentication

## Troubleshooting

### Common Issues

1. **Connection refused to MySQL**
   - Verify MySQL is running: `sudo service mysql status`
   - Check credentials in `.env` file
   - Ensure database exists: `CREATE DATABASE IF NOT EXISTS db_order;`

2. **RabbitMQ Connection Issues**
   - Verify RabbitMQ is running: `sudo rabbitmqctl status`
   - Check credentials and virtual host permissions
   - Ensure required exchanges and queues exist

3. **Service Discovery Issues**
   - Verify Eureka server is running
   - Check network connectivity
   - Verify service registration in Eureka dashboard

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.