package ru.suai.diplom.ratelimit.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.Refill
import io.github.bucket4j.distributed.proxy.ProxyManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.suai.diplom.models.User
import java.time.Duration
import java.util.function.Supplier


@Service
class RateLimiter(
    val userService: UserService,
) {
    @Autowired
    lateinit var proxyManager: ProxyManager<String>

    fun resolveBucket(key: String): Bucket {
        val configSupplier: Supplier<BucketConfiguration> = getConfigSupplierForUser(key)
        return proxyManager.builder().build(key, configSupplier)
    }

    private fun getConfigSupplierForUser(userId: String): Supplier<BucketConfiguration> {
//        val user: User = userService.getUserById(userId.toLong()) //for tarif
        val refill = Refill.intervally(1, Duration.ofDays(1))
        val limit = Bandwidth.classic(1, refill)
        return Supplier<BucketConfiguration> {
            BucketConfiguration.builder()
                .addLimit(limit)
                .build()
        }
    }
}