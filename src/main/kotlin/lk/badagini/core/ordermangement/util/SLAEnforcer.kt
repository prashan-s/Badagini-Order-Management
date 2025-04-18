package lk.badagini.core.ordermangement.util

import lk.badagini.core.ordermangement.domain.OrderStatus
import lk.badagini.core.ordermangement.repository.OrderRepository
import lk.badagini.core.ordermangement.service.IOrderService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SLAEnforcer(
    private val orderRepository: OrderRepository,
    private val orderService: IOrderService
) {
    @Scheduled(fixedDelay = 30000) // 30 seconds
    fun enforceTimeLimits() {
        val now = LocalDateTime.now()

        // NEW → CONFIRMED: 5 min
        orderRepository.findByStatusAndOrderTimeBefore(OrderStatus.NEW, now.minusMinutes(5))
            .forEach { order ->
                orderService.updateOrderStatus(order.orderId, "CANCELLED", "SLA_ENFORCER")
            }

        // CONFIRMED → PREPARING: 3 min
        orderRepository.findByStatusAndOrderTimeBefore(OrderStatus.CONFIRMED, now.minusMinutes(3))
            .forEach { order ->
                orderService.updateOrderStatus(order.orderId, "CANCELLED", "SLA_ENFORCER")
            }

        // PREPARING → READY: 30 min
        orderRepository.findByStatusAndOrderTimeBefore(OrderStatus.PREPARING, now.minusMinutes(30))
            .forEach { order ->
                orderService.updateOrderStatus(order.orderId, "EXPIRED", "SLA_ENFORCER")
            }

        // READY → PICKED_UP: 10 min
        orderRepository.findByStatusAndOrderTimeBefore(OrderStatus.READY_FOR_PICKUP, now.minusMinutes(10))
            .forEach { order ->
                orderService.updateOrderStatus(order.orderId, "EXPIRED", "SLA_ENFORCER")
            }

        // PICKED_UP → DELIVERED: 45 min
        orderRepository.findByStatusAndOrderTimeBefore(OrderStatus.PICKED_UP, now.minusMinutes(45))
            .forEach { order ->
                orderService.updateOrderStatus(order.orderId, "FAILED_DELIVERY", "SLA_ENFORCER")
            }
    }
} 