package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.suai.diplom.models.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}