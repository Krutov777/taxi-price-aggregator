package ru.suai.diplom.models

import ru.suai.diplom.dto.request.SignUpForm
import javax.persistence.*

@Entity
@Table(name = "account")
open class User(
    @Enumerated(value = EnumType.STRING)
    open var state: State? = null,
    @Enumerated(value = EnumType.STRING)
    open var role: Role? = null,
    open var login: String? = null,
    open var email: String? = null,
    @Column(name = "first_name")
    open var firstName: String? = null,
    @Column(name = "last_name")
    open var lastName: String? = null,
    open var password: String? = null,
    @Column(name = "confirm_code")
    open var confirmCode: String? = null,
    @OneToMany(mappedBy = "user")
    open var orderHistory: MutableSet<OrderHistory>? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
) {
    enum class State {
        NOT_CONFIRMED, CONFIRMED
    }

    enum class Role {
        USER
    }

    companion object {
        fun from(form: SignUpForm): User {
            return User(
                firstName = form.firstName,
                lastName = form.lastName,
                login = form.login,
                email = form.email,
                role = Role.valueOf(form.role.toString())
            )
        }
    }
}
