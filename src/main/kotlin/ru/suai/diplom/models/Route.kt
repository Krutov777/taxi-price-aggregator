package ru.suai.diplom.models

import javax.persistence.*

@Entity
open class Route(
    @Column(name = "from_address")
    open var fromAddress: String,
    @Column(name = "toAddress")
    open var toAddress: String,
    @Column(name = "from_latitude")
    open var fromLatitude: Double,
    @Column(name = "from_longitude")
    open var fromLongitude: Double,
    @Column(name = "to_latitude")
    open var toLatitude: Double,
    @Column(name = "to_longitude")
    open var toLongitude: Double,

    @OneToMany(mappedBy = "route")
    open var orderHistory: MutableSet<OrderHistory>,
    @OneToMany(mappedBy = "route")
    open var orderPrice: MutableSet<OrderPrice>,
    @OneToMany(mappedBy = "route")
    open var price: MutableList<Price>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
)
