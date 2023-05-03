package ru.suai.diplom.security.repositories

interface WhiteListRepository {
    fun save(accessToken: String, refreshToken: String, email: String)

    fun findByEmail(email: String): List<String>?

    fun existsByRefreshToken(refreshToken: String, email: String): Boolean

    fun exists(token: String, email: String): Boolean

    fun delete(email: String)
}