package lk.badagini.core.ordermangement.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_status_history")
data class OrderStatusHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @Column
    @Enumerated(EnumType.STRING)
    val previous: OrderStatus?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val next: OrderStatus,

    @Column(nullable = false)
    val actor: String,

    @Column(nullable = false)
    val changedAt: LocalDateTime = LocalDateTime.now()
) 