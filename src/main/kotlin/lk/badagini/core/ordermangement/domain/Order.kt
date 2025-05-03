package lk.badagini.core.ordermangement.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val orderId: Long = 0,

    @Column(nullable = false)
    val customerUserId: Long,

    @Column(nullable = false)
    val restaurantId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.NEW,

    @Column(nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @Column(columnDefinition = "JSON")
    val deliveryAddress: String,

    @Column(nullable = false)
    val orderTime: LocalDateTime = LocalDateTime.now(),

    @Column
    var deliveryTime: LocalDateTime? = null,

    @Column(columnDefinition = "DOUBLE DEFAULT NULL")
    val latitude: Double? = null,

    @Column(columnDefinition = "DOUBLE DEFAULT NULL")
    val longitude: Double? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItem> = mutableListOf(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val statusHistory: MutableList<OrderStatusHistory> = mutableListOf()
)

enum class OrderStatus {
    NEW,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    DELIVERED,
    CANCELLED,
    REJECTED,
    EXPIRED,
    FAILED_PAYMENT,
    FAILED_DELIVERY,
    RETURNED_TO_STORE
} 