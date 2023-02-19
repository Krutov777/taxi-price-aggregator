package ru.suai.diplom.security.details

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.suai.diplom.repositories.UserRepository

@Service
class AccountUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails? {
        return AccountUserDetails(
            userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found")
        )
    }
}