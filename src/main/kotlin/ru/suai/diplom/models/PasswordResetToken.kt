package ru.suai.diplom.models

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "password_reset_token")
open class PasswordResetToken(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,
    open var token: String? = null,

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    open var user: User? = null,
    open var expiryDate: Date? = null
)