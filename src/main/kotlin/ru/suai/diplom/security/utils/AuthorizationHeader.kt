package ru.suai.diplom.security.utils

import javax.servlet.http.HttpServletRequest

interface AuthorizationHeaderUtil {
    fun hasAuthorizationToken(request: HttpServletRequest): Boolean
    fun getToken(request: HttpServletRequest): String
}
