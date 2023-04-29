package ru.suai.diplom.security.utils

import org.springframework.security.core.Authentication

interface JwtUtil {
    data class ParsedToken(
        val email: String? = null,
        val role: String? = null
    ) {}

    fun generateTokens(subject: String, authority: String, issuer: String): MutableMap<String, String>
    fun buildAuthentication(token: String): Authentication
    fun parse(token: String): ParsedToken
}

