package ru.suai.diplom.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import ru.suai.diplom.models.RefreshToken
import ru.suai.diplom.models.User
import ru.suai.diplom.repositories.RefreshTokenRepository
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.security.authentication.RefreshTokenAuthentication
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.security.utils.AuthorizationHeaderUtil
import ru.suai.diplom.security.utils.JwtUtil
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenAuthenticationFilter(
    private val objectMapper: ObjectMapper,
    private val jwtUtil: JwtUtil,
    private val authorizationHeaderUtil: AuthorizationHeaderUtil,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : UsernamePasswordAuthenticationFilter(authenticationConfiguration.authenticationManager) {

    companion object {
        const val USERNAME_PARAMETER = "email"
        const val AUTHENTICATION_URL = "/taxi-aggregator/api/auth/token"
    }

    init {
        this.usernameParameter = USERNAME_PARAMETER
        this.setFilterProcessesUrl(AUTHENTICATION_URL)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return if (
            hasRefreshToken(request) &&
            refreshTokenRepository.findByToken(authorizationHeaderUtil.getToken(request)) != null
        ) {
            val refreshTokenAuthentication = RefreshTokenAuthentication(authorizationHeaderUtil.getToken(request))
            super.getAuthenticationManager().authenticate(refreshTokenAuthentication)
        } else {
            super.attemptAuthentication(request, response)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        writeTokens(request, response, authResult.principal as UserDetails)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
    }

    @Throws(IOException::class)
    private fun writeTokens(
        request: HttpServletRequest,
        response: HttpServletResponse,
        userDetails: UserDetails
    ) {
        response.contentType = "application/json"

        val email = userDetails.username
        val tokenJson: Map<String, String> = jwtUtil.generateTokens(
            email!!,
            userDetails.authorities.iterator().next()!!.authority,
            request.requestURL.toString()
        )

        val token = tokenJson["refreshToken"]
        val user: User = userRepository.findByEmail(email) ?: User()
        val refreshToken: RefreshToken = RefreshToken(
            token = token,
            user = user
        )
        if (refreshTokenRepository.findByUserEmail(email) != null) {
            refreshTokenRepository.findByUserEmail(email)!!.id?.let { refreshTokenRepository.deleteById(it) }
            refreshTokenRepository.save(refreshToken)
        }
        refreshTokenRepository.save(refreshToken)
        objectMapper.writeValue(response.outputStream, tokenJson)
    }

    private fun hasRefreshToken(request: HttpServletRequest): Boolean {
        return authorizationHeaderUtil.hasAuthorizationToken(request)
    }
}
