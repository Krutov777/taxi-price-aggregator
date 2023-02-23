package ru.suai.diplom.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import ru.suai.diplom.exceptions.BadRoleException
import ru.suai.diplom.models.User
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class SignUpForm(
    @Schema(description = "Имя")
    @field:NotBlank
    @field:Length(min = 2, max = 15)
    var firstName: String? = null,

    @Schema(description = "Фамилия")
    @field:NotNull
    @field:Length(min = 1, max = 20)
    var lastName: String? = null,

    @Schema(description = "Логин")
    @field:NotNull
    @field:Length(min = 1, max = 20)
    var login: String? = null,

    @Schema(description = "Email")
    @field:Email(message = "Email не подходит")
    @field:NotBlank
    var email: String? = null,

    @Schema(description = "Пароль")
    @field:NotBlank
    @field:Length(min = 8, max = 25)
    var password: String? = null,

    @Schema(description = "Повторный пароль")
    @field:NotBlank
    var repeatPassword: String? = null,

    @Schema(description = "Роль")
    @field:NotBlank
    var role: String? = null
) {
    val isValidRole
        get() =
            if (role !in User.Role.values().map { it.toString() })
                throw BadRoleException("Такой роли не существует!")
            else role

}
