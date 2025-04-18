package lk.badagini.core.ordermangement.events

import lk.badagini.core.ordermangement.domain.Order
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderEventPublisher(
    private val rabbitTemplate: RabbitTemplate
) {

    companion object {
        private val log = LoggerFactory.getLogger(OrderEventPublisher::class.java)
    }

    fun publishOrderStatusChange(order: Order, source: String) {
        val event = OrderEventPayload(
            orderId = order.orderId,
            status = order.status,
            timestamp = LocalDateTime.now(),
            source = source
        )

        val routingKey = "order.${order.status.name.lowercase()}"
        val exchange = "order.events"

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event)
            log.info("📤 Published event to RabbitMQ: exchange='{}', routingKey='{}', payload={}", exchange, routingKey, event)
        } catch (e: Exception) {
            log.error("❌ Failed to publish event: exchange='{}', routingKey='{}', payload={}, error={}",
                exchange, routingKey, event, e.message, e)
            throw e
        }
    }
}

data class OrderEventPayload(
    val orderId: Long,
    val status: lk.badagini.core.ordermangement.domain.OrderStatus,
    val timestamp: LocalDateTime,
    val source: String
) 