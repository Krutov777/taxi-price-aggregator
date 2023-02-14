package ru.suai.diplom.models

import javax.persistence.*

@Entity
open class Taxi(
    open var name: String,
    @OneToMany(mappedBy = "taxi")
    open var price: MutableList<Price>,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
)
