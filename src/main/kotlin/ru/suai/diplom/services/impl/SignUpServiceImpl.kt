package ru.suai.diplom.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import ru.suai.diplom.exceptions.*
import ru.suai.diplom.models.User
import ru.suai.diplom.repositories.RefreshTokenRepository
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.services.SignUpService
import ru.suai.diplom.utils.constants.GlobalConstants.OCCUPIED_EMAIL
import ru.suai.diplom.utils.constants.GlobalConstants.OCCUPIED_LOGIN
import ru.suai.diplom.utils.constants.GlobalConstants.PASSWORDS_DONT_MATCH
import ru.suai.diplom.utils.constants.GlobalConstants.UNAUTHORIZED
import ru.suai.diplom.utils.constants.GlobalConstants.USER_NOT_FOUND
import ru.suai.diplom.utils.emails.EmailUtil
import java.util.*
import javax.transaction.Transactional

@Service
class SignUpServiceImpl(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailUtil: EmailUtil
) : SignUpService {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Transactional
    override fun singUp(form: SignUpForm): SignUpResponse {
        form.isValidRole
        val user: User = User.from(form)
        user.password = passwordEncoder.encode(form.password)
        user.state = User.State.NOT_CONFIRMED
        user.confirmCode = UUID.randomUUID().toString()
        if (userRepository.findByEmail(user.email ?: "") != null) {
            throw OccupiedEmailException(OCCUPIED_EMAIL)
        }
        if (userRepository.findByLogin(user.login ?: "") != null) {
            throw OccupiedLoginException(OCCUPIED_LOGIN)
        }
        if (form.password != form.repeatPassword) {
            throw BadPasswordException(PASSWORDS_DONT_MATCH)
        }
        userRepository.save(user)
        emailUtil.sendMail(user, "Confirmation");
        return SignUpResponse.from(user)
    }

    @Transactional
    override fun signOut(authentication: Authentication?): String {
        if (authentication == null) {
            throw object : AuthenticationException(UNAUTHORIZED) {}
        } else {
            val userDetails = authentication.principal as? UserDetails
            val email: String? = userDetails?.username
            email?.let { userRepository.findByEmail(it) ?: throw UserNotFoundException(USER_NOT_FOUND) }
                ?.let { refreshTokenRepository.deleteByUser(it) }
            return "Произведен успешный выход из аккаунта - $email" ?: ""
        }
    }

    override fun confirm(confirmCode: String?) {
        val user = userRepository.findByConfirmCode(confirmCode.toString())
        if (user == null) {
            throw EntityNotFoundException("Confirm code doesn't exist")
        } else {
            user.state = User.State.CONFIRMED
            userRepository.save(user)
        }
    }
}
