package ru.suai.diplom.models

import javax.persistence.*

@Entity
@Table(name = "account")
open class User(
    @Enumerated(value = EnumType.STRING)
    open var state: State,
    open var login: String,
    open var email: String,
    @Column(name = "first_name")
    open var firstName: String,
    @Column(name = "last_name")
    open var lastName: String,
    open var password: String,
    @OneToMany(mappedBy = "user")
    open var orderHistory: MutableSet<OrderHistory>,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
) {
    enum class State {
        NOT_CONFIRMED, CONFIRMED
    }
}
