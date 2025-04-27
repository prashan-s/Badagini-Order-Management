package lk.badagini.core.ordermangement.service

import lk.badagini.core.ordermangement.domain.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IOrderService {
    fun createOrder(order: Order): Order
    fun getOrder(orderId: Long): Order
    fun getCustomerOrders(customerId: Long, pageable: Pageable): Page<Order>
    fun getMerchantOrders(restaurantId: Long, pageable: Pageable): Page<Order>
    fun updateOrderStatus(orderId: Long, status: String, actor: String): Order
    fun cancelOrder(orderId: Long, actor: String): Order
} 