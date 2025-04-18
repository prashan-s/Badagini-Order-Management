package lk.badagini.core.ordermangement.controller

import com.fasterxml.jackson.databind.ObjectMapper
import lk.badagini.core.ordermangement.domain.Order
import lk.badagini.core.ordermangement.domain.OrderItem
import lk.badagini.core.ordermangement.dto.*
import lk.badagini.core.ordermangement.service.IOrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: IOrderService,
    private val objectMapper: ObjectMapper
) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val order = Order(
            customerUserId = request.customerUserId,
            restaurantId = request.restaurantId,
            totalAmount = request.items.sumOf { it.itemPrice * it.quantity.toBigDecimal() },
            deliveryAddress = objectMapper.writeValueAsString(request.deliveryAddress),
            items = request.items.map { item ->
                OrderItem(
                    order = Order(customerUserId = request.customerUserId, restaurantId = request.restaurantId, totalAmount = item.itemPrice, deliveryAddress = ""),
                    menuItemId = item.menuItemId,
                    itemName = item.itemName,
                    quantity = item.quantity,
                    itemPrice = item.itemPrice
                )
            }.toMutableList()
        )
        
        return ResponseEntity(OrderResponse.fromEntity(orderService.createOrder(order)), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(OrderResponse.fromEntity(orderService.getOrder(id)))
    }

    @GetMapping("/customer/{userId}")
    fun getCustomerOrders(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<OrderResponse>> {
        return ResponseEntity.ok(orderService.getCustomerOrders(userId, pageable).map { OrderResponse.fromEntity(it) })
    }

    @GetMapping("/merchant/{restaurantId}/queue")
    fun getMerchantOrders(
        @PathVariable restaurantId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<OrderResponse>> {
        return ResponseEntity.ok(orderService.getMerchantOrders(restaurantId, pageable).map { OrderResponse.fromEntity(it) })
    }

    @PatchMapping("/{id}/cancel")
    fun cancelOrder(
        @PathVariable id: Long,
        @RequestParam actor: String
    ): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(OrderResponse.fromEntity(orderService.cancelOrder(id, actor)))
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestParam status: String,
        @RequestParam actor: String
    ): ResponseEntity<OrderResponse> {
        return ResponseEntity.ok(OrderResponse.fromEntity(orderService.updateOrderStatus(id, status, actor)))
    }
} 