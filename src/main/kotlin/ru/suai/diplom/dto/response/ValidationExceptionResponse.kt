package ru.suai.diplom.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "Список ошибок валидации")
data class ValidationExceptionResponse(
    private var errors: List<ValidationErrorDto>? = null
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Ошибка валидации")
    data class ValidationErrorDto(
        private val exception: String? = null,
        var objectName: String? = null,
        private val message: String? = null
    )
}