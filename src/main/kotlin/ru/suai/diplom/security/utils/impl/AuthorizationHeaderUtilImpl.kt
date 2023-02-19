package ru.suai.diplom.security.utils.impl

import org.springframework.stereotype.Component
import ru.suai.diplom.security.utils.AuthorizationHeaderUtil
import javax.servlet.http.HttpServletRequest

@Component
class AuthorizationHeaderUtilImpl : AuthorizationHeaderUtil {
    companion object {
        private const val AUTHORIZATION_HEADER_NAME = "AUTHORIZATION"
        private const val BEARER = "Bearer "
    }

    override fun hasAuthorizationToken(request: HttpServletRequest): Boolean {
        val header = request.getHeader(AUTHORIZATION_HEADER_NAME)
        return header != null && header.startsWith(BEARER)
    }

    override fun getToken(request: HttpServletRequest): String {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_NAME)
        return authorizationHeader?.substring(BEARER.length) ?: ""
    }
}
