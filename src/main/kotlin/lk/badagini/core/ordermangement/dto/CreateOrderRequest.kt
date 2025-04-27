package lk.badagini.core.ordermangement.dto

import java.math.BigDecimal

data class CreateOrderRequest(
    val restaurantId: Long,
    val items: List<OrderItemRequest>,
    val deliveryAddress: DeliveryAddressRequest,
    val latitude: Double,
    val longitude: Double
)

data class OrderItemRequest(
    val menuItemId: Long,
    val itemName: String,
    val quantity: Int,
    val itemPrice: BigDecimal,
    val latitude: Double,
    val longitude: Double
)

data class DeliveryAddressRequest(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val additionalInstructions: String?
) 