package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Информация о пользователе")
data class UserInfoResponse(
    @Schema(description = "Имя")
    val firstName: String,

    @Schema(description = "Логин")
    val login: String,

    @Schema(description = "Фамилия")
    val lastName: String,

    @Schema(description = "Почта")
    val email: String
)
