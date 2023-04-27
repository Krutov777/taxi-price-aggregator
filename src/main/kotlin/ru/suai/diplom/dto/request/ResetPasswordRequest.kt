package ru.suai.diplom.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ResetPasswordRequest (
    @Schema(description = "Токен восстановления пароля")
    @field:NotNull
    var token: String? = null,

    @Schema(description = "Новый пароль")
    @field:NotBlank
    @field:Length(min = 8, max = 25)
    var newPassword: String? = null,
)
