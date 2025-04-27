package lk.badagini.core.ordermangement.repository

import lk.badagini.core.ordermangement.domain.Order
import lk.badagini.core.ordermangement.domain.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByCustomerUserId(customerUserId: Long, pageable: Pageable): Page<Order>
    fun findByRestaurantId(restaurantId: Long, pageable: Pageable): Page<Order>
    fun findByStatusAndOrderTimeBefore(status: OrderStatus, time: LocalDateTime): List<Order>
} 