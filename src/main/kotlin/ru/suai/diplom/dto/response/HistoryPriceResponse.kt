package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "История цен")
data class HistoryPrice(
    @Schema(description = "Адрес 'Откуда'")
    val fromAddress: String,
    @Schema(description = "Адрес 'Куда'")
    val toAddress: String,
    @Schema(description = "Долгота 'Откуда'")
    val fromLongitude: Double,
    @Schema(description = "Широта 'Откуда'")
    val fromLatitude: Double,
    @Schema(description = "Долгота 'Куда'")
    val toLongitude: Double,
    @Schema(description = "Широта 'Куда'")
    val toLatitude: Double,
    @Schema(description = "История цен для маршрута")
    val prices: List<TaxiPriceResponse>
)

@Schema(description = "История цен")
data class HistoryPriceResponse(
    @Schema(description = "Список историй цен")
    val historyPrices: List<HistoryPrice>,
)
