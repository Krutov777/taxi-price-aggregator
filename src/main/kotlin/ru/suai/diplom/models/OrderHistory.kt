package ru.suai.diplom.models

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order_history")
open class OrderHistory(
    @Enumerated(value = EnumType.STRING)
    open var status: Status,
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    open var dateTime: Date? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User,
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    open var route: Route,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null,
) {
    enum class Status {
        COMPLETED, IN_WORK
    }
}
