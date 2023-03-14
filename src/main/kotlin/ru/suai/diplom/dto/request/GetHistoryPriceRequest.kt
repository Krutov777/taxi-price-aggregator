package ru.suai.diplom.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(description = "Форма для получения цен такси")
data class GetHistoryPriceRequest(
    @Schema(description = "Адрес 'Откуда'")
    @field:NotBlank
    var fromAddress: String,

    @Schema(description = "Адрес 'Куда'")
    @field:NotBlank
    var toAddress: String,

    @Schema(description = "Долгота 'Откуда'")
    @field:NotNull
    var longitudeFrom: Double,

    @Schema(description = "Широта 'Откуда'")
    @field:NotNull
    var latitudeFrom: Double,

    @Schema(description = "Долгота 'Куда'")
    @field:NotNull
    var longitudeBefore: Double,

    @Schema(description = "Широта 'Куда'")
    @field:NotNull
    var latitudeBefore: Double
)
