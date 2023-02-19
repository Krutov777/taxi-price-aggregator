package ru.suai.diplom.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import ru.suai.diplom.security.authentication.RefreshTokenAuthentication
import ru.suai.diplom.security.details.AccountUserDetails
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
    private val authenticationConfiguration: AuthenticationConfiguration
) : UsernamePasswordAuthenticationFilter(authenticationConfiguration.authenticationManager) {

    companion object {
        const val USERNAME_PARAMETER = "email"
        const val AUTHENTICATION_URL = "/auth/token"
    }

    init {
        this.usernameParameter = USERNAME_PARAMETER
        this.setFilterProcessesUrl(AUTHENTICATION_URL)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return if (hasRefreshToken(request)) {
            val refreshToken: String = authorizationHeaderUtil.getToken(request)
            val refreshTokenAuthentication = RefreshTokenAuthentication(refreshToken)
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
        writeTokens(request, response, authResult.principal as AccountUserDetails)
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
        userDetails: AccountUserDetails
    ) {
        response.contentType = "application/json"
        val tokenJson: Map<String, String> = jwtUtil.generateTokens(
            userDetails.username.toString(),
            userDetails.authorities.iterator().next()?.authority ?: "",
            request.requestURL.toString()
        )
        objectMapper.writeValue(response.outputStream, tokenJson)
    }

    private fun hasRefreshToken(request: HttpServletRequest): Boolean {
        return authorizationHeaderUtil.hasAuthorizationToken(request)
    }
}
