package ru.suai.diplom.services

import ru.suai.diplom.dto.response.TaxiPriceResponse

interface PriceService {
    fun getCurrentPrices(
        fromAddress: String,
        toAddress: String,
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): List<TaxiPriceResponse>
}