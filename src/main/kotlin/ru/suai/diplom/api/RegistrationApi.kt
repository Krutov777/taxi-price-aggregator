package ru.suai.diplom.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.suai.diplom.dto.request.ResetPasswordRequest
import ru.suai.diplom.dto.request.SignUpForm
import ru.suai.diplom.dto.response.SignUpResponse
import javax.validation.Valid
import javax.validation.constraints.NotEmpty


@Validated
@Schema(name = "Registration Controller", description = "sign up")
@RequestMapping("taxi-aggregator/api/")
interface RegistrationApi {
    @Operation(summary = "Регистрация")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "Пользователь зарегистрирован",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = SignUpResponse::class))]
        ),
            ApiResponse(responseCode = "400", description = "Ошибка при регистрации"),
            ApiResponse(responseCode = "429", description = "Слишком много запросов")
        ]
    )
    @PostMapping(
        value = ["/signup"],
        produces = [APPLICATION_JSON_VALUE],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun signUp(@Valid @RequestBody form: SignUpForm): ResponseEntity<SignUpResponse>

    @Operation(summary = "Logout")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Пользователь вышел из аккаунта"
        ),
            ApiResponse(responseCode = "400", description = "Ошибка при logout"),
            ApiResponse(responseCode = "429", description = "Слишком много запросов")
        ]
    )
    @PostMapping(
        value = ["/signout"]
    )
    fun signOut(authentication: Authentication?): ResponseEntity<Any>

    @Operation(summary = "Подтверждение пользователя")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Пользователь подтверждён",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]
        ),
            ApiResponse(responseCode = "400", description = "Код подтверждения не существует"),
            ApiResponse(responseCode = "429", description = "Слишком много запросов")
        ]
    )
    @GetMapping(value = ["/confirm/{confirm-code}"], produces = [APPLICATION_JSON_VALUE])
    fun confirmUser(
        @Valid @NotEmpty @Parameter(description = "confirmation code") @PathVariable("confirm-code") confirmCode: String
    ): ResponseEntity<HttpStatus>

    @Operation(summary = "Создание и отправка ссылки на восстановление пароля по почте")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Ссылка на смену пароля отправлена",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]
        ),
            ApiResponse(responseCode = "400", description = "Не удалось отправить ссылку на восстановление пароля для данного email адреса"),
            ApiResponse(responseCode = "429", description = "Слишком много запросов")
        ]
    )
    @PostMapping(value = ["/user/reset_password"], produces = [APPLICATION_JSON_VALUE])
    fun createLinkForResetPassword(
        @Valid @NotEmpty @Parameter(description = "email") @RequestParam("email") email: String
    ): ResponseEntity<HttpStatus>

    @Operation(summary = "Обновление пароля пользователя")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "Пароль успешно изменен",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]
        ),
            ApiResponse(responseCode = "400", description = "Не удалось изменить пароль"),
            ApiResponse(responseCode = "429", description = "Слишком много запросов")
        ]
    )
    @PutMapping(value = ["/user/update_password"], produces = [APPLICATION_JSON_VALUE])
    fun updatePassword(
        @Valid @RequestBody resetPasswordRequest: ResetPasswordRequest
    ): ResponseEntity<HttpStatus>

}
