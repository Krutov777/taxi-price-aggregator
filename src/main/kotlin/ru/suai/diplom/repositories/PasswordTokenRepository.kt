package ru.suai.diplom.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import ru.suai.diplom.models.PasswordResetToken
import ru.suai.diplom.models.User

@Repository
interface PasswordTokenRepository : JpaRepository<PasswordResetToken, Long> {
    fun findByToken(token: String): PasswordResetToken?

    @Modifying
    fun deleteByUser(user: User)
}