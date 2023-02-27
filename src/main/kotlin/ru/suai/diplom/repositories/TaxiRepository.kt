package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.Taxi

@Repository
interface TaxiRepository: JpaRepository<Taxi, Long> {
    fun findByName(name: String): Taxi?
}