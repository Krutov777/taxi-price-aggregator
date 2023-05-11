package ru.suai.diplom.config

import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.grid.jcache.JCacheProxyManager
import org.ehcache.config.CacheConfiguration
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.core.config.DefaultConfiguration
import org.ehcache.jsr107.EhcacheCachingProvider
import org.redisson.config.Config
import org.redisson.jcache.configuration.RedissonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import javax.cache.CacheManager
import javax.cache.Caching


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

    @Bean
    fun config(): Config {
        val config = Config()
        config.useSingleServer().address = "redis://localhost:6379"
        return config
    }

    @Bean(name = ["SpringCM"])
    fun cacheManager(config: Config): CacheManager {
        val cacheConfiguration = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(
                String::class.java,
                String::class.java, ResourcePoolsBuilder.heap(10000L)
            )
            .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofDays(2L)))
            .withDispatcherConcurrency(4)
            .build()
        val cacheMap: MutableMap<String, CacheConfiguration<*, *>> = HashMap()
        cacheMap["vsk"] = cacheConfiguration
        val ehcacheCachingProvider =
            Caching.getCachingProvider(EhcacheCachingProvider::class.java.name) as EhcacheCachingProvider
        val defaultConfiguration = DefaultConfiguration(cacheMap, ehcacheCachingProvider.defaultClassLoader)
        val cacheManager =
            ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.defaultURI, defaultConfiguration)
        cacheManager.createCache("cache", RedissonConfiguration.fromConfig<Any, Any>(config))
        cacheManager.createCache("userList", RedissonConfiguration.fromConfig<Any, Any>(config))
        cacheManager.createCache("buckets", RedissonConfiguration.fromConfig<Any, Any>(config))
        return cacheManager
    }
    @Bean
    fun proxyManager(cacheManager: CacheManager): ProxyManager<String> {
        return JCacheProxyManager(cacheManager.getCache("cache"))
    }
}