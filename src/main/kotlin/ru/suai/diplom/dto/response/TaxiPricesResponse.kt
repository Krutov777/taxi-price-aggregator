package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Список цен на такси")
data class TaxiPricesResponse(
    val taxiPrices: List<TaxiPriceResponse>
)
