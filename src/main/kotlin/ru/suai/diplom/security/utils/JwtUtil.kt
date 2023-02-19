package ru.suai.diplom.security.utils

import org.springframework.security.core.Authentication

interface JwtUtil {
    fun generateTokens(subject: String, authority: String, issuer: String): MutableMap<String, String>
    fun buildAuthentication(token: String): Authentication
}

