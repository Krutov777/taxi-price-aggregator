package ru.suai.diplom.exceptions

class UserNotFoundException : EntityNotFoundException {
    constructor() {}
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable) : super(message, cause) {}
}