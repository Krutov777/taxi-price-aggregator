package ru.suai.diplom.models

import javax.persistence.*

@Entity(name = "refresh_token")
open class RefreshToken(
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: User? = null,

    @Column(nullable = false, unique = true)
    open var token: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
)
