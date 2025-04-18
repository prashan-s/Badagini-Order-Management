package lk.badagini.core.ordermangement.dto

import java.math.BigDecimal

data class CreateOrderRequest(
    val customerUserId: Long,
    val restaurantId: Long,
    val items: List<OrderItemRequest>,
    val deliveryAddress: DeliveryAddressRequest
)

data class OrderItemRequest(
    val menuItemId: Long,
    val itemName: String,
    val quantity: Int,
    val itemPrice: BigDecimal
)

data class DeliveryAddressRequest(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val additionalInstructions: String?
) 