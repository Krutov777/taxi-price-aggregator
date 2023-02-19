package ru.suai.diplom.security.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.suai.diplom.security.filters.JwtTokenAuthenticationFilter
import ru.suai.diplom.security.filters.JwtTokenAuthorizationFilter

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class JwtSecurityConfiguration(
    var userDetailsServiceImpl: UserDetailsService,
    var refreshTokenAuthenticationProvider: AuthenticationProvider,
    var passwordEncoder: PasswordEncoder
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        jwtAuthenticationFilter: JwtTokenAuthenticationFilter?,
        jwtAuthorizationFilter: JwtTokenAuthorizationFilter?
    ): SecurityFilterChain {
        httpSecurity.csrf().disable()
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.authorizeRequests()
            .antMatchers("/**").permitAll()
        httpSecurity.authorizeRequests().antMatchers("/swagger-ui.html/**").permitAll()
        httpSecurity.addFilter(jwtAuthenticationFilter)
        httpSecurity.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }

    @Autowired
    @Throws(Exception::class)
    fun bindUserDetailsServiceAndPasswordEncoder(builder: AuthenticationManagerBuilder) {
        builder.authenticationProvider(refreshTokenAuthenticationProvider)
        builder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder)
    }
}
