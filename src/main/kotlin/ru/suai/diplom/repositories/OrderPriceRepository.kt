package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.OrderPrice

@Repository
interface OrderPriceRepository: JpaRepository<OrderPrice, Long> {
}