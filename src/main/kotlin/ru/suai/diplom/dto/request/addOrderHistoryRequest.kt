package ru.suai.diplom.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(description = "Форма добавления заказа истории цен")
data class addOrderHistoryRequest(
    @Schema(description = "Адрес 'Откуда'")
    @field:NotBlank
    @field:NotNull
    var fromAddress: String? = null,

    @Schema(description = "Адрес 'Куда'")
    @field:NotBlank
    @field:NotNull
    var toAddress: String? = null,

    @Schema(description = "Долгота 'Откуда'")
    @field:NotNull
    var longitudeFrom: Double? = null,

    @Schema(description = "Широта 'Откуда'")
    @field:NotNull
    var latitudeFrom: Double? = null,

    @Schema(description = "Долгота 'Куда'")
    @field:NotNull
    var latitudeTo: Double? = null,

    @Schema(description = "Широта 'Куда'")
    @field:NotNull
    var longitudeTo: Double? = null,
)