package ru.suai.diplom.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.suai.diplom.dto.response.ValidationExceptionResponse
import ru.suai.diplom.exceptions.AccessException
import ru.suai.diplom.exceptions.BadPasswordException
import ru.suai.diplom.exceptions.EntityNotFoundException
import ru.suai.diplom.exceptions.OccupiedEmailException
import java.util.function.Consumer

@RestControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAccessDeniedException(exception: HttpMessageNotReadableException): ResponseEntity<ValidationExceptionResponse> {
        val response: ValidationExceptionResponse = ValidationExceptionResponse(
            errors = listOf(
                ValidationExceptionResponse.ValidationErrorDto(message = exception.message)
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body<ValidationExceptionResponse>(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<ValidationExceptionResponse> {
        val errors: MutableList<ValidationExceptionResponse.ValidationErrorDto> =
            ArrayList()
        exception.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val errorDto: ValidationExceptionResponse.ValidationErrorDto =
                ValidationExceptionResponse.ValidationErrorDto(message = error.defaultMessage)
            var objectName: String? = null
            objectName = if (error is FieldError) {
                error.field
            } else {
                error.objectName
            }
            errorDto.objectName = objectName
            errors.add(errorDto)
        })
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationExceptionResponse(errors = errors))
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(exception: AccessDeniedException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(value = [AuthenticationException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(value = [AccessException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessException(exception: AccessException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(BadPasswordException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadPasswordException(exception: BadPasswordException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = "Registration",
                        exception = exception.javaClass.canonicalName,
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(OccupiedEmailException::class)
    fun handleOccupiedEmailException(exception: OccupiedEmailException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = "Registration",
                        exception = exception.javaClass.canonicalName,
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(exception: EntityNotFoundException): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = "Registration",
                        exception = exception.javaClass.canonicalName,
                        message = exception.message
                    )
                )
            )
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAnyException(exception: Exception): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = "Registration",
                        exception = exception.javaClass.canonicalName,
                        message = exception.message
                    )
                )
            )
        )
    }
}