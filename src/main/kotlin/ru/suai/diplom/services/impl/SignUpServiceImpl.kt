package ru.suai.diplom.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import ru.suai.diplom.exceptions.BadPasswordException
import ru.suai.diplom.exceptions.OccupiedEmailException
import ru.suai.diplom.models.User
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.services.SignUpService
import ru.suai.diplom.utils.constants.GlobalConstants.OCCUPIED_EMAIL
import ru.suai.diplom.utils.constants.GlobalConstants.PASSWORDS_DONT_MATCH
import java.util.*

@Service
class SignUpServiceImpl(
    private val userRepository: UserRepository,
) : SignUpService {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun singUp(form: SignUpForm): SignUpResponse {
        val user: User = User.from(form)
        user.password = passwordEncoder.encode(form.password)
        user.state = User.State.NOT_CONFIRMED
        user.confirmCode = UUID.randomUUID().toString()
        if (userRepository.findByEmail(user.email ?: "") != null) {
            throw OccupiedEmailException(OCCUPIED_EMAIL)
        }
        if (form.password != form.repeatPassword) {
            throw BadPasswordException(PASSWORDS_DONT_MATCH)
        }
        userRepository.save(user)
        return SignUpResponse.from(user)
    }

    override fun signOut(authentication: Authentication) {
    }
}