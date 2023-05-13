package ru.suai.diplom.services

import org.springframework.security.core.Authentication
import ru.suai.diplom.dto.request.addOrderHistoryRequest
import ru.suai.diplom.dto.response.HistoryPriceResponse
import ru.suai.diplom.dto.response.TaxiPricesResponse

interface PriceService {
    fun getCurrentPrices(
        fromAddress: String,
        toAddress: String,
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): TaxiPricesResponse

    fun addOrderHistoryPrice(
        addOrderHistoryRequest: addOrderHistoryRequest,
        authentication: Authentication?
    )

    fun getHistoryPrice(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double,
//        page: Int
    ): HistoryPriceResponse

    fun getHistoryPrice(
        authentication: Authentication?,
//        page: Int
    ): HistoryPriceResponse

}