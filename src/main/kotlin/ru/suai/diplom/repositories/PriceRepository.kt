package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.Price
import ru.suai.diplom.models.Route

@Repository
interface PriceRepository: JpaRepository<Price, Long> {
    fun findByRoute(route: Route): List<Price>
}