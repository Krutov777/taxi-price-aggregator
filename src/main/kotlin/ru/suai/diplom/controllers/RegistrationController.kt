package ru.suai.diplom.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import ru.suai.diplom.api.RegistrationApi
import ru.suai.diplom.dto.request.ResetPasswordRequest
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import ru.suai.diplom.services.SignUpService


@RestController
class RegistrationController(private val signUpService: SignUpService) : RegistrationApi {
    override fun signUp(form: SignUpForm): ResponseEntity<SignUpResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(signUpService.singUp(form))
    }

    override fun signOut(authentication: Authentication?): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(signUpService.signOut(authentication))
    }

    override fun confirmUser(confirmCode: String): ResponseEntity<HttpStatus> {
        signUpService.confirm(confirmCode)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    override fun createLinkForResetPassword(email: String): ResponseEntity<HttpStatus> {
        signUpService.createLinkForResetPassword(email)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    override fun updatePassword(resetPasswordRequest: ResetPasswordRequest): ResponseEntity<HttpStatus> {
        signUpService.updatePassword(resetPasswordRequest)
        return ResponseEntity.status(HttpStatus.OK).build()
    }


}
