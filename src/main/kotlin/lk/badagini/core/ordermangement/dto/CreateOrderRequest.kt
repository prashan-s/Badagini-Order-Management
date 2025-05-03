package lk.badagini.core.ordermangement.dto

import java.math.BigDecimal

data class CreateOrderRequest(
    val restaurantId: String,
    val items: List<OrderItemRequest>,
    val deliveryAddress: DeliveryAddressRequest,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class OrderItemRequest(
    val menuItemId: String,
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