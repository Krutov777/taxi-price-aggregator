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
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler
import org.springframework.security.web.firewall.RequestRejectedHandler
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.suai.diplom.security.filters.JwtTokenAuthenticationFilter
import ru.suai.diplom.security.filters.JwtTokenAuthorizationFilter


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class JwtSecurityConfiguration(
    var userDetailsServiceImpl: UserDetailsService,
    var refreshTokenAuthenticationProvider: AuthenticationProvider,
    var passwordEncoder: PasswordEncoder,
) {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        jwtAuthenticationFilter: JwtTokenAuthenticationFilter,
        jwtAuthorizationFilter: JwtTokenAuthorizationFilter
    ): SecurityFilterChain {
        httpSecurity.cors().and().csrf().disable()
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.authorizeRequests()
            .antMatchers("/**").permitAll()
        httpSecurity.authorizeRequests().antMatchers("/swagger-ui.html/**").permitAll()
        httpSecurity.addFilter(jwtAuthenticationFilter)
        httpSecurity.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }

    // To enable CORS
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        val allowedOriginsUrl: MutableList<String> = ArrayList()
        allowedOriginsUrl.add("http://localhost:8081/")
        allowedOriginsUrl.add("http://192.168.1.2:8081/")
        configuration.allowedOrigins = allowedOriginsUrl // www - obligatory
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun configureFirewall(): HttpFirewall {
        val strictHttpFirewall = StrictHttpFirewall()
        strictHttpFirewall.setAllowedHttpMethods(
            listOf(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        ) // Allow only HTTP GET, POST, DELETE and OPTIONS methods
        return strictHttpFirewall
    }

    /*
     Use this bean if you are using Spring Security 5.4 and above
     */
    @Bean
    fun requestRejectedHandler(): RequestRejectedHandler {
        return HttpStatusRequestRejectedHandler() // Default status code is 400. Can be customized
    }

    @Autowired
    @Throws(Exception::class)
    fun bindUserDetailsServiceAndPasswordEncoder(builder: AuthenticationManagerBuilder) {
        builder.authenticationProvider(refreshTokenAuthenticationProvider)
        builder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder)
    }
}
