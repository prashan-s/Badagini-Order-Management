package lk.badagini.core.ordermangement.events

import lk.badagini.core.ordermangement.domain.Order
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderEventPublisher(
    private val rabbitTemplate: RabbitTemplate
) {
    fun publishOrderStatusChange(order: Order, source: String) {
        val event = OrderEventPayload(
            orderId = order.orderId,
            status = order.status,
            timestamp = LocalDateTime.now(),
            source = source
        )
        
        rabbitTemplate.convertAndSend(
            "order.events",
            "order.${order.status.name.lowercase()}",
            event
        )
    }
}

data class OrderEventPayload(
    val orderId: Long,
    val status: lk.badagini.core.ordermangement.domain.OrderStatus,
    val timestamp: LocalDateTime,
    val source: String
) 