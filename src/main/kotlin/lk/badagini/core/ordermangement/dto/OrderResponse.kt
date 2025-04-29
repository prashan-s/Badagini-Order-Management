package lk.badagini.core.ordermangement.dto

import lk.badagini.core.ordermangement.domain.Order
import lk.badagini.core.ordermangement.domain.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val customerUserId: Long,
    val restaurantId: String,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val deliveryAddress: String,
    val orderTime: LocalDateTime,
    val deliveryTime: LocalDateTime?,
    val items: List<OrderItemResponse>,
    val statusHistory: List<StatusHistoryResponse>
) {
    companion object {
        fun fromEntity(order: Order): OrderResponse {
            return OrderResponse(
                orderId = order.orderId,
                customerUserId = order.customerUserId,
                restaurantId = order.restaurantId,
                status = order.status,
                totalAmount = order.totalAmount,
                deliveryAddress = order.deliveryAddress,
                orderTime = order.orderTime,
                deliveryTime = order.deliveryTime,
                items = order.items.map { OrderItemResponse.fromEntity(it) },
                statusHistory = order.statusHistory.map { StatusHistoryResponse.fromEntity(it) }
            )
        }
    }
}

data class OrderItemResponse(
    val orderItemId: Long,
    val menuItemId: Long,
    val itemName: String,
    val quantity: Int,
    val itemPrice: BigDecimal
) {
    companion object {
        fun fromEntity(orderItem: lk.badagini.core.ordermangement.domain.OrderItem): OrderItemResponse {
            return OrderItemResponse(
                orderItemId = orderItem.orderItemId,
                menuItemId = orderItem.menuItemId,
                itemName = orderItem.itemName,
                quantity = orderItem.quantity,
                itemPrice = orderItem.itemPrice
            )
        }
    }
}

data class StatusHistoryResponse(
    val id: Long,
    val previous: OrderStatus?,
    val next: OrderStatus,
    val actor: String,
    val changedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(history: lk.badagini.core.ordermangement.domain.OrderStatusHistory): StatusHistoryResponse {
            return StatusHistoryResponse(
                id = history.id,
                previous = history.previous,
                next = history.next,
                actor = history.actor,
                changedAt = history.changedAt
            )
        }
    }
}