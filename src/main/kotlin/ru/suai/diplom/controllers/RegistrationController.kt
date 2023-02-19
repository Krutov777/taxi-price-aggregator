package ru.suai.diplom.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.suai.diplom.api.RegistrationApi
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
}
