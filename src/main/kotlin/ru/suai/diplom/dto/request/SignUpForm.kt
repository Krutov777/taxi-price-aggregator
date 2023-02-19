package ru.suai.diplom.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length
import ru.suai.diplom.models.User
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class SignUpForm(
    @Schema(description = "Имя")
    var firstName: @NotBlank @Length(min = 2, max = 15) String? = null,

    @Schema(description = "Фамилия")
    var lastName: @NotNull @Length(min = 1, max = 20) String? = null,

    @Schema(description = "Логин")
    var login: @NotNull @Length(min = 1, max = 20) String? = null,

    @Schema(description = "Email")
    var email: @Email(message = "Email не подходит") String? = null,

    @Schema(description = "Пароль")
    var password: @NotBlank @Length(min = 8, max = 25) String? = null,

    @Schema(description = "Повторный пароль")
    var repeatPassword: @NotBlank String? = null,

    @Schema(description = "Роль")
    var role: @NotBlank String? = null
) {
    companion object {
        fun from(user: User): SignUpForm {
            return SignUpForm(
                firstName = user.firstName,
                lastName = user.lastName,
                login = user.login,
                email = user.email,
                password = user.password,
                role = user.role?.name
            )
        }
    }
}
