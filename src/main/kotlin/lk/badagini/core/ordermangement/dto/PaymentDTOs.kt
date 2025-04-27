package lk.badagini.core.ordermangement.dto

import java.math.BigDecimal

data class PaymentInitiationRequest(
    val orderId: Long,
    val amount: BigDecimal,
    val customerId: Long
)

data class CreateOrderResponse(
    val order: OrderResponse,
    val paymentRequired: Boolean,
    val totalAmount: BigDecimal
) 