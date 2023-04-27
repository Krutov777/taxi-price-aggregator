package ru.suai.diplom.exceptions


class ResetPasswordTokenException: RuntimeException{
    constructor(msg: String) : super(msg)
    constructor(message: String?, cause: Throwable) : super(message, cause)
}