package ru.suai.diplom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Цена на такси")
data class TaxiPriceResponse(
    @Schema(description = "Имя сервиса такси")
    var nameTaxi: String? = null,
    @Schema(description = "Стоимость")
    var price: Double? = null,
    @Schema(description = "Валюта")
    var currency: String? = null,
)