package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "История цен")
data class HistoryPriceResponse(
    @Schema(description = "История цен, ключ - дата и время в миллисекундах")
    val historyPrices: Map<Long, List<TaxiPriceResponse>>
)
