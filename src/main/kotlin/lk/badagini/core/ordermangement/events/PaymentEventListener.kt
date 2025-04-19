package lk.badagini.core.ordermangement.events

import lk.badagini.core.ordermangement.service.IOrderService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PaymentEventListener(
    private val orderService: IOrderService
) {
    companion object {
        private val log = LoggerFactory.getLogger(PaymentEventListener::class.java)
    }

    @RabbitListener(queues = ["payment-success-queue"])
    fun handlePaymentAuthorized(event: PaymentEventPayload) {
        log.info("Received payment authorization for order ${event.orderId}: ${event.result}")
        if (event.result == "SUCCESS") {
            orderService.updateOrderStatus(event.orderId, "CONFIRMED", "PAYMENT_SERVICE")
            log.info("Order ${event.orderId} confirmed after successful payment")
        }
    }

    @RabbitListener(queues = ["payment-failed-queue"])
    fun handlePaymentFailed(event: PaymentEventPayload) {
        log.info("Received payment failure for order ${event.orderId}")
        orderService.updateOrderStatus(event.orderId, "FAILED_PAYMENT", "PAYMENT_SERVICE")
        log.warn("Order ${event.orderId} marked as failed payment")
    }
}

data class PaymentEventPayload(
    val orderId: Long,
    val result: String
) 