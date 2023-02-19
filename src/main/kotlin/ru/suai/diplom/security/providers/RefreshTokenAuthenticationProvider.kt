package ru.suai.diplom.security.providers

import com.auth0.jwt.exceptions.JWTVerificationException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import ru.suai.diplom.exceptions.RefreshTokenException
import ru.suai.diplom.security.authentication.RefreshTokenAuthentication
import ru.suai.diplom.security.utils.JwtUtil


@Component
class RefreshTokenAuthenticationProvider(private var jwtUtil: JwtUtil) : AuthenticationProvider {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as String
        return try {
            jwtUtil.buildAuthentication(token)
        } catch (e: JWTVerificationException) {
            log.info(e.message)
            throw RefreshTokenException(e.message, e)
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return RefreshTokenAuthentication::class.java.isAssignableFrom(authentication)
    }
}