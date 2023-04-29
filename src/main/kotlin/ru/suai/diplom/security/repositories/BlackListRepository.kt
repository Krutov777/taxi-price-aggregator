package ru.suai.diplom.security.repositories

interface BlackListRepository {
    fun save(accessToken: String, refreshToken: String)

    fun exists(token: String): Boolean
}