package lk.badagini.core.ordermangement.service.impl

import lk.badagini.core.ordermangement.domain.Order
import lk.badagini.core.ordermangement.domain.OrderStatus
import lk.badagini.core.ordermangement.domain.OrderStatusHistory
import lk.badagini.core.ordermangement.events.OrderEventPublisher
import lk.badagini.core.ordermangement.repository.OrderRepository
import lk.badagini.core.ordermangement.service.IOrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderEventPublisher: OrderEventPublisher
) : IOrderService {

    @Transactional
    override fun createOrder(order: Order): Order {
        order.statusHistory.add(
            OrderStatusHistory(
                order = order,
                previous = null,
                next = OrderStatus.NEW,
                actor = "CUSTOMER"
            )
        )
        val savedOrder = orderRepository.save(order)
        orderEventPublisher.publishOrderStatusChange(savedOrder, "CUSTOMER")
        return savedOrder
    }

    override fun getOrder(orderId: Long): Order {
        return orderRepository.findById(orderId)
            .orElseThrow { NoSuchElementException("Order not found with id: $orderId") }
    }

    override fun getCustomerOrders(customerId: Long, pageable: Pageable): Page<Order> {
        return orderRepository.findByCustomerUserId(customerId, pageable)
    }

    override fun getMerchantOrders(restaurantId: Long, pageable: Pageable): Page<Order> {
        return orderRepository.findByRestaurantId(restaurantId, pageable)
    }

    @Transactional
    override fun updateOrderStatus(orderId: Long, status: String, actor: String): Order {
        val order = getOrder(orderId)
        val newStatus = OrderStatus.valueOf(status)
        
        if (!isValidStatusTransition(order.status, newStatus)) {
            throw IllegalStateException("Invalid status transition from ${order.status} to $newStatus")
        }

        val previousStatus = order.status
        order.status = newStatus

        if (newStatus == OrderStatus.DELIVERED) {
            order.deliveryTime = LocalDateTime.now()
        }

        order.statusHistory.add(
            OrderStatusHistory(
                order = order,
                previous = previousStatus,
                next = newStatus,
                actor = actor
            )
        )

        val savedOrder = orderRepository.save(order)
        orderEventPublisher.publishOrderStatusChange(savedOrder, actor)
        return savedOrder
    }

    @Transactional
    override fun cancelOrder(orderId: Long, actor: String): Order {
        val order = getOrder(orderId)
        
        if (!canCancel(order.status)) {
            throw IllegalStateException("Cannot cancel order in status: ${order.status}")
        }

        val previousStatus = order.status
        order.status = OrderStatus.CANCELLED

        order.statusHistory.add(
            OrderStatusHistory(
                order = order,
                previous = previousStatus,
                next = OrderStatus.CANCELLED,
                actor = actor
            )
        )

        val savedOrder = orderRepository.save(order)
        orderEventPublisher.publishOrderStatusChange(savedOrder, actor)
        return savedOrder
    }

    private fun isValidStatusTransition(current: OrderStatus, next: OrderStatus): Boolean {
        return when (current) {
            OrderStatus.NEW -> next in setOf(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.CANCELLED, OrderStatus.FAILED_PAYMENT)
            OrderStatus.CONFIRMED -> next in setOf(OrderStatus.PREPARING, OrderStatus.CANCELLED)
            OrderStatus.PREPARING -> next in setOf(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED)
            OrderStatus.READY_FOR_PICKUP -> next == OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> next in setOf(OrderStatus.DELIVERED, OrderStatus.FAILED_DELIVERY)
            OrderStatus.FAILED_DELIVERY -> next == OrderStatus.RETURNED_TO_STORE
            else -> false
        }
    }

    private fun canCancel(status: OrderStatus): Boolean {
        return status in setOf(OrderStatus.NEW, OrderStatus.CONFIRMED, OrderStatus.PREPARING)
    }
} 