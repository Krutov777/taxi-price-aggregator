package ru.suai.diplom.security.utils.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import ru.suai.diplom.models.User
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.security.utils.JwtUtil
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtUtilImpl(
    @Value("\${jwt.secretKey}")
    private val secret: String
) : JwtUtil {

    companion object {
        private const val ACCESS_TOKEN_EXPIRES_TIME: Long = 1 * 60 * 60 * 1000 // ONE HOUR
        private const val REFRESH_TOKEN_EXPIRES_TIME: Long = 24 * 60 * 60 * 1000 // 24 HOURS
    }

    data class ParsedToken(
        val email: String? = null,
        val role: String? = null
    ) {}

    override fun generateTokens(subject: String, authority: String, issuer: String): MutableMap<String, String> {
        val algorithm = Algorithm.HMAC256(secret.toByteArray(StandardCharsets.UTF_8))
        val accessToken = JWT.create()
            .withSubject(subject)
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRES_TIME))
            .withClaim("role", authority)
            .withIssuer(issuer)
            .sign(algorithm)
        val refreshToken = JWT.create()
            .withSubject(subject)
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRES_TIME))
            .withClaim("role", authority)
            .withIssuer(issuer)
            .sign(algorithm)
        val tokens: MutableMap<String, String> = HashMap()
        tokens["accessToken"] = accessToken
        tokens["refreshToken"] = refreshToken
        return tokens
    }

    override fun buildAuthentication(token: String): Authentication {
        val parsedToken = parse(token)
        return UsernamePasswordAuthenticationToken(
            UserDetails(
                User(
                    role = User.Role.valueOf(parsedToken.role.toString()),
                    email = parsedToken.email
                ),

                ), null, setOf(SimpleGrantedAuthority(parsedToken.role))
        )
    }

    @Throws(JWTVerificationException::class)
    private fun parse(token: String): ParsedToken {
        val algorithm = Algorithm.HMAC256(secret.toByteArray(StandardCharsets.UTF_8))
        val jwtVerifier = JWT.require(algorithm).build()
        val decodedJWT = jwtVerifier.verify(token)
        val email = decodedJWT.subject
        val role = decodedJWT.getClaim("role").asString()
        return ParsedToken(
            role = role,
            email = email
        )
    }

}