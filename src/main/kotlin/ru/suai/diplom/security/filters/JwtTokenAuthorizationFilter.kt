package ru.suai.diplom.security.filters

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.suai.diplom.security.filters.JwtTokenAuthenticationFilter.Companion.AUTHENTICATION_URL
import ru.suai.diplom.security.repositories.WhiteListRepository
import ru.suai.diplom.security.utils.AuthorizationHeaderUtil
import ru.suai.diplom.security.utils.JwtUtil
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenAuthorizationFilter(
    var jwtUtil: JwtUtil,
    var authorizationHeaderUtil: AuthorizationHeaderUtil,
    private val whiteListRepository: WhiteListRepository
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath == AUTHENTICATION_URL) {
            filterChain.doFilter(request, response)
        } else {
            if (authorizationHeaderUtil.hasAuthorizationToken(request)) {
                val jwt: String = authorizationHeaderUtil.getToken(request)
                return try {
                    if (!whiteListRepository.exists(jwt, jwtUtil.parse(jwt).email.toString())) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    } else {
                        val authenticationToken: Authentication = jwtUtil.buildAuthentication(jwt)
                        SecurityContextHolder.getContext().authentication = authenticationToken
                        filterChain.doFilter(request, response)
                    }
                } catch (e: Exception) {
                    log.info(e.message)
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }
}