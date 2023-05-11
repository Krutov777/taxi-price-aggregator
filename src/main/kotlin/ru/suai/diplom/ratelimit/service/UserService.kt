package ru.suai.diplom.ratelimit.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import org.springframework.stereotype.Service
import ru.suai.diplom.exceptions.UserNotFoundException
import ru.suai.diplom.models.User
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.utils.constants.GlobalConstants


@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getUserByAuthentication(authentication: Authentication?): User {
        if (authentication == null)
            throw object : AuthenticationException(GlobalConstants.UNAUTHORIZED) {}
        val userDetails = authentication.principal as? UserDetails
        return userRepository.findByEmail(
            userDetails?.username ?: ""
        ) ?: throw UserNotFoundException(GlobalConstants.USER_NOT_FOUND)
    }

    @Cacheable(value = ["userList"], key = "#user")
    fun getUserById(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("Пользователь не найден")
    }

    @CacheEvict(value = ["userList"], allEntries = true)
    @Scheduled(fixedDelayString = "\${caching.spring.userListTTL}", initialDelay = 86400000) //one day
    fun deleteUserList() {
        logger.info("Исключить список пользователей")
    }
}