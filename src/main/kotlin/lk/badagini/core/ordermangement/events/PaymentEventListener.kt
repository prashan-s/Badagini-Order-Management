package lk.badagini.core.ordermangement.events

import lk.badagini.core.ordermangement.service.IOrderService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PaymentEventListener(
    private val orderService: IOrderService
) {
    @RabbitListener(queues = ["payment.authorized"])
    fun handlePaymentAuthorized(event: PaymentEventPayload) {
        if (event.result == "SUCCESS") {
            orderService.updateOrderStatus(event.orderId, "CONFIRMED", "PAYMENT_SERVICE")
        }
    }

    @RabbitListener(queues = ["payment.failed"])
    fun handlePaymentFailed(event: PaymentEventPayload) {
        orderService.updateOrderStatus(event.orderId, "FAILED_PAYMENT", "PAYMENT_SERVICE")
    }
}

data class PaymentEventPayload(
    val orderId: Long,
    val result: String
) 