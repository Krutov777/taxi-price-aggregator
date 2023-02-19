package ru.suai.diplom.models

import java.util.*
import javax.persistence.*

@Entity
open class Price(
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    open var dateTime: Date,
    open var price: Int,
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    open var route: Route,
    @ManyToOne
    @JoinColumn(name = "taxi_id", nullable = false)
    open var taxi: Taxi,
    @ManyToOne
    @JoinColumn(name = "order_price_id", nullable = true)
    open var orderPrice: OrderPrice?,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
)