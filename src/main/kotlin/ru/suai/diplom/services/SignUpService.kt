package ru.suai.diplom.services

import org.springframework.security.core.Authentication
import ru.suai.diplom.dto.request.ResetPasswordRequest
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import ru.suai.diplom.dto.response.UserInfoResponse

interface SignUpService {
    fun singUp(form: SignUpForm): SignUpResponse

    fun signOut(authentication: Authentication?): String

    fun confirm(confirmCode: String?)

    fun createLinkForResetPassword(email: String?)

    fun validatePasswordResetToken(token: String?): String?

    fun updatePassword(resetPasswordRequest: ResetPasswordRequest)

    fun getUserInfo(authentication: Authentication?): UserInfoResponse
}