package ru.suai.diplom.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
class RedisConfig {

    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    lateinit var redisPort: String

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, List<String>>? {
        val template = RedisTemplate<String, List<String>>()
        template.connectionFactory = connectionFactory
        // Add some specific configuration here. Key serializers, etc.
        return template
    }
}