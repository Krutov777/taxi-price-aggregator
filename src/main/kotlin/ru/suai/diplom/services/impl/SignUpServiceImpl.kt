package ru.suai.diplom.services.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.suai.diplom.dto.request.ResetPasswordRequest
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import ru.suai.diplom.exceptions.*
import ru.suai.diplom.models.PasswordResetToken
import ru.suai.diplom.models.User
import ru.suai.diplom.repositories.PasswordTokenRepository
import ru.suai.diplom.repositories.UserRepository
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.security.repositories.WhiteListRepository
import ru.suai.diplom.services.SignUpService
import ru.suai.diplom.utils.constants.GlobalConstants.OCCUPIED_EMAIL
import ru.suai.diplom.utils.constants.GlobalConstants.OCCUPIED_LOGIN
import ru.suai.diplom.utils.constants.GlobalConstants.PASSWORDS_DONT_MATCH
import ru.suai.diplom.utils.constants.GlobalConstants.RESET_PASSWORD_TOKEN_EXPIRES_DATE
import ru.suai.diplom.utils.constants.GlobalConstants.UNAUTHORIZED
import ru.suai.diplom.utils.emails.EmailUtil
import java.util.*
import javax.transaction.Transactional


@Service
class SignUpServiceImpl(
    private val userRepository: UserRepository,
    private val passwordTokenRepository: PasswordTokenRepository,
    private val emailUtil: EmailUtil,
    private val whiteListRepository: WhiteListRepository,
) : SignUpService {

    private val logger = LoggerFactory.getLogger(javaClass)

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
        emailUtil.sendLinkForConfirmUser(user, "Подтверждение");
        return SignUpResponse.from(user)
    }

    @Transactional
    override fun signOut(authentication: Authentication?): String {
        if (authentication == null) {
            throw object : AuthenticationException(UNAUTHORIZED) {}
        } else {
            val userDetails = authentication.principal as? UserDetails
            val email: String? = userDetails?.username
            whiteListRepository.delete(email.toString())
            return "Произведен успешный выход из аккаунта - $email"
        }
    }

    override fun confirm(confirmCode: String?) {
        val user = userRepository.findByConfirmCode(confirmCode.toString())
        if (user == null) {
            throw EntityNotFoundException("Код подтверждения не существует")
        } else {
            user.state = User.State.CONFIRMED
            userRepository.save(user)
        }
    }

    @Transactional
    override fun createLinkForResetPassword(email: String?) {
        val user = userRepository.findByEmail(email.toString())
            ?: throw EntityNotFoundException("Пользователь с таким email - ${email.toString()} не существует")
        val token = UUID.randomUUID().toString()
        createPasswordResetTokenForUser(user, token, Date(System.currentTimeMillis() + RESET_PASSWORD_TOKEN_EXPIRES_DATE))
        emailUtil.sendLinkForResetPassword(user, token,"Восстановление пароля");
    }

    fun createPasswordResetTokenForUser(user: User, token: String, expiryDate: Date) {
        passwordTokenRepository.deleteByUser(user)
        passwordTokenRepository.save(
            PasswordResetToken(
                expiryDate = expiryDate,
                token = token,
                user = user
            )
        )
    }

    override fun validatePasswordResetToken(token: String?): String? {
        val passToken: PasswordResetToken? = passwordTokenRepository.findByToken(token ?: "")
        return if (!isTokenFound(passToken)) "Неверная ссылка для восстановления пароля" else if (passToken?.let { isTokenExpired(it) } == true) "Срок действия ссылки истек" else null
    }

    override fun updatePassword(resetPasswordRequest: ResetPasswordRequest) {
        logger.info(resetPasswordRequest.toString())
        val result = validatePasswordResetToken(resetPasswordRequest.token)
        if (result != null)
            throw ResetPasswordTokenException(result)
        val user = passwordTokenRepository.findByToken(resetPasswordRequest.token ?: "")?.user
        if (user != null) {
            whiteListRepository.delete(user.email.toString())
            changeUserPassword(user, resetPasswordRequest.newPassword)
        }
        else
            throw UserNotFoundException("Пользователя с таким токеном восстановления пароля не существует")

    }

    fun changeUserPassword(user: User, password: String?) {
        user.password = passwordEncoder.encode(password)
        userRepository.save(user)
    }

    private fun isTokenFound(passToken: PasswordResetToken?): Boolean {
        return passToken != null
    }

    private fun isTokenExpired(passToken: PasswordResetToken): Boolean {
        val cal = Calendar.getInstance()
        return passToken.expiryDate?.before(cal.time) ?: false
    }
}
