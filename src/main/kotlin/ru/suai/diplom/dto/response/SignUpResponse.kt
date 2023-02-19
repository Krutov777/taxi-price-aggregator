package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import ru.suai.diplom.models.User

@Schema(description = "Аккаунт")
data class SignUpResponse(
    @Schema(description = "Роль")
    var role: User.Role? = null,

    @Schema(description = "Состояние", example = "CONFIRMED")
    var state: User.State? = null,

    @Schema(description = "Имя")
    var firstName: String? = null,

    @Schema(description = "Логин")
    var login: String? = null,

    @Schema(description = "Фамилия")
    var lastName: String? = null,

    @Schema(description = "Почта")
    var email: String? = null,
) {
    companion object {
        fun from(user: User): SignUpResponse {
            return SignUpResponse(
                firstName = user.firstName,
                lastName = user.lastName,
                login = user.login,
                email = user.email,
                role = User.Role.valueOf(user.role.toString()),
                state = User.State.valueOf(user.state.toString())
            )
        }
    }
}
