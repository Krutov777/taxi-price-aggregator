package ru.suai.diplom.security.authentication

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class RefreshTokenAuthentication(private val refreshToken: String) : Authentication {

    override fun getName(): String? = null

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? = null

    override fun getCredentials(): Any = refreshToken

    override fun getDetails(): Any? = null

    override fun getPrincipal(): Any? = null

    override fun isAuthenticated() = false

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
    }

}
