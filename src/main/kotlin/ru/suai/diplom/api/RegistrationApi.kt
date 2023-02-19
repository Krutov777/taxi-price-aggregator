package ru.suai.diplom.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import javax.validation.Valid

@Schema(name = "Registration Controller", description = "sign up")
@RequestMapping("taxi-aggregator/api/")
interface RegistrationApi {
    @Operation(summary = "Регистрация")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Пользователь зарегистрирован",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = SignUpResponse::class))]
        ), ApiResponse(responseCode = "400", description = "Ошибка при регистрации")]
    )
    @PostMapping(
        value = ["/signUp"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun signUp(@RequestBody form: @Valid SignUpForm): ResponseEntity<SignUpResponse>
}
