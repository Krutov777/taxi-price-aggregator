package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.RefreshToken
import ru.suai.diplom.models.User

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun findByUserEmail(email: String): RefreshToken?

    @Modifying
    fun deleteByUser(user: User)
}