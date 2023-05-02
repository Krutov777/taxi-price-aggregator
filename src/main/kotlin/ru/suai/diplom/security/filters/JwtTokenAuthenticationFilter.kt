package ru.suai.diplom.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.security.authentication.RefreshTokenAuthentication
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.security.repositories.BlackListRepository
import ru.suai.diplom.security.repositories.WhiteListRepository
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
    private val blacklistRepository: BlackListRepository,
    private val whiteListRepository: WhiteListRepository
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
        val refreshToken = authorizationHeaderUtil.getToken(request)
        return try {
            return if (
                hasRefreshToken(request) && whiteListRepository.existsByRefreshToken(
                    refreshToken,
                    jwtUtil.parse(refreshToken).email.toString()
                )
            ) {
                val refreshTokenAuthentication = RefreshTokenAuthentication(authorizationHeaderUtil.getToken(request))
                super.getAuthenticationManager().authenticate(refreshTokenAuthentication)
            } else {
                super.attemptAuthentication(request, response)
            }
        }
        catch (e: Exception) {
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
        val whiteList = whiteListRepository.findByEmail(email ?: "")
        if (whiteList != null) {
            blacklistRepository.save(whiteList[0], whiteList[1])
        }
        val tokenJson: Map<String, String> = jwtUtil.generateTokens(
            email!!,
            userDetails.authorities.iterator().next()!!.authority,
            request.requestURL.toString()
        )

        whiteListRepository.save(tokenJson["accessToken"]!!, tokenJson["refreshToken"]!!, email)
        objectMapper.writeValue(response.outputStream, tokenJson)
    }

    private fun hasRefreshToken(request: HttpServletRequest): Boolean {
        return authorizationHeaderUtil.hasAuthorizationToken(request)
    }
}
