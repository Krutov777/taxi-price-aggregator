package ru.suai.diplom.security.repositories.impl


import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import ru.suai.diplom.security.repositories.BlackListRepository

@Repository
class BlackListRepositoryRedisImpl(
    private val redisTemplate: RedisTemplate<String, List<String>>
) : BlackListRepository {
    override fun save(accessToken: String, refreshToken: String) {
        val blackList = redisTemplate.opsForValue().get("blackList") as? MutableList ?: mutableListOf()
        blackList.add(accessToken)
        blackList.add(refreshToken)
        redisTemplate.opsForValue().set("blackList", blackList)
    }

    override fun exists(token: String): Boolean {
        return redisTemplate.opsForValue().get("blackList")?.find { it == token } != null
    }
}