package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.OrderHistory

@Repository
interface OrderHistoryRepository : JpaRepository<OrderHistory, Long> {
    @Query(
        value = "select * from order_history where status = ?1",
        nativeQuery = true
    )
    fun findAll(status: String) : List<OrderHistory>
}