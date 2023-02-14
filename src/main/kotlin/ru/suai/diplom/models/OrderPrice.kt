package ru.suai.diplom.models

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order_price")
open class OrderPrice(
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    open var dateTime: Date,
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    open var route: Route,
    @OneToMany(mappedBy = "price")
    open var price: MutableSet<Price>,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
)
