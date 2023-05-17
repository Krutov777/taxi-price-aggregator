package ru.suai.diplom.ratelimit.filter

import io.github.bucket4j.Bucket
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.suai.diplom.ratelimit.service.RateLimiter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class RequestFilter(
    private val rateLimiter: RateLimiter,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.method == HttpMethod.POST.toString() && request.requestURI.equals("/taxi-aggregator/api/prices/history")){
            val userId = rateLimiter.userService.getUserByAuthentication(SecurityContextHolder.getContext().authentication).id
            if (userId != null) {
                val bucket: Bucket = rateLimiter.resolveBucket(userId.toString())
                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response)
                } else
                    sendErrorReponse(response, HttpStatus.TOO_MANY_REQUESTS.value())
            } else
                sendErrorReponse(response, HttpStatus.FORBIDDEN.value())
        } else
            filterChain.doFilter(request, response)
    }

    private fun sendErrorReponse(response: HttpServletResponse, value: Int) {
        response.status = value
        response.contentType = MediaType.APPLICATION_JSON_VALUE
    }
}