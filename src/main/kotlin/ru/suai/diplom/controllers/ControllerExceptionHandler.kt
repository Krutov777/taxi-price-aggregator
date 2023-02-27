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
import ru.suai.diplom.exceptions.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class ControllerExceptionHandler {
    val dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleAccessDeniedException(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        val errors = mutableListOf<ValidationExceptionResponse.ValidationErrorDto>()
        exception.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val errorMessage = error.defaultMessage
            val errorDto: ValidationExceptionResponse.ValidationErrorDto =
                ValidationExceptionResponse.ValidationErrorDto(
                    message = errorMessage,
                    objectName = exception.javaClass.toString(),
                    exception = exception.javaClass.canonicalName,
                    path = request.requestURI.toString(),
                    timestamp = DateTimeFormatter
                        .ofPattern(dateFormat)
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now())
                )
            val objectName: String? = when (error) {
                is FieldError -> error.field
                else -> error.objectName
            }
            errorDto.objectName = objectName
            errors.add(errorDto)
        })
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationExceptionResponse(errors = errors))
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(
        exception: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(value = [AuthenticationException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(
        exception: AuthenticationException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(value = [AccessException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessException(
        exception: AccessException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(BadPasswordException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadPasswordException(
        exception: BadPasswordException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(OccupiedEmailException::class)
    fun handleOccupiedEmailException(
        exception: OccupiedEmailException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(OccupiedLoginException::class)
    fun handleOccupiedLoginException(
        exception: OccupiedLoginException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(
        exception: EntityNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(value = [BadRoleException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRoleException(
        exception: BadRoleException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(exception: ConstraintViolationException, request: HttpServletRequest): ResponseEntity<ValidationExceptionResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ValidationExceptionResponse(
                errors = listOf(
                    ValidationExceptionResponse.ValidationErrorDto(
                        objectName = exception.javaClass.toString(),
                        exception = exception.javaClass.canonicalName,
                        message = exception.message,
                        path = request.requestURI.toString(),
                        timestamp = DateTimeFormatter
                            .ofPattern(dateFormat)
                            .withZone(ZoneOffset.UTC)
                            .format(Instant.now())
                    )
                )
            )
        )
    }

}