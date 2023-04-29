package ru.suai.diplom.security.repositories.impl

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import ru.suai.diplom.security.repositories.WhiteListRepository

@Repository
class WhiteListRepositoryRedisImpl(
    private val redisTemplate: RedisTemplate<String, List<String>>
) : WhiteListRepository {
    override fun save(accessToken: String, refreshToken: String, email: String) {
        redisTemplate.delete(email)
        val whiteList = listOf(accessToken, refreshToken)
        redisTemplate.opsForValue().set(email, whiteList)
    }

    override fun findByEmail(email: String): List<String>? {
        return redisTemplate.opsForValue().get(email)
    }

    override fun existsByRefreshToken(refreshToken: String, email: String): Boolean {
        return redisTemplate.opsForValue().get(email)?.get(1) == refreshToken
    }
}