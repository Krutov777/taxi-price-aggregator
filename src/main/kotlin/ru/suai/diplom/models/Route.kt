package ru.suai.diplom.models

import javax.persistence.*

@Entity
open class Route(
    @Column(name = "from_address")
    open var fromAddress: String? = null,
    @Column(name = "toAddress")
    open var toAddress: String? = null,
    @Column(name = "from_latitude")
    open var fromLatitude: Double? = null,
    @Column(name = "from_longitude")
    open var fromLongitude: Double? = null,
    @Column(name = "to_latitude")
    open var toLatitude: Double? = null,
    @Column(name = "to_longitude")
    open var toLongitude: Double? = null,

    @OneToMany(mappedBy = "route")
    open var orderHistory: MutableSet<OrderHistory>? = null,
    @OneToMany(mappedBy = "route")
    open var orderPrice: MutableSet<OrderPrice>? = null,
    @OneToMany(mappedBy = "route")
    open var price: MutableList<Price>? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
)
