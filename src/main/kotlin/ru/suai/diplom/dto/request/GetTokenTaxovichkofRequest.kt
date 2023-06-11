package ru.suai.diplom.dto.request

data class GetTokenTaxovichkofRequest(
    val phone: String?,
    val password: String?,
    val key: String,
    val slug: String,
)
