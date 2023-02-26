package ru.suai.diplom.services

import org.springframework.security.core.Authentication
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse

interface SignUpService {
    fun singUp(form: SignUpForm): SignUpResponse

    fun signOut(authentication: Authentication?): String

    fun confirm(confirmCode: String?)
}