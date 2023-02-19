package ru.suai.diplom.exceptions

import org.springframework.security.core.AuthenticationException

class RefreshTokenException : AuthenticationException {
    constructor(msg: String) : super(msg)
    constructor(message: String?, cause: Throwable) : super(message, cause)
}