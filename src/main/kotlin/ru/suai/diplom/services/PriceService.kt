package ru.suai.diplom.services

import org.springframework.security.core.Authentication
import ru.suai.diplom.dto.request.addOrderHistoryRequest
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

    fun addOrderHistoryPrice(
        addOrderHistoryRequest: addOrderHistoryRequest,
        authentication: Authentication?
    )

    fun getHistoryPrice(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): List<List<TaxiPriceResponse>>

    fun getHistoryPrice(
        authentication: Authentication?
    ): List<TaxiPriceResponse>

}