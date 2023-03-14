package ru.suai.diplom.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import ru.suai.diplom.api.PriceApi
import ru.suai.diplom.dto.request.addOrderHistoryRequest
import ru.suai.diplom.dto.response.TaxiPriceResponse
import ru.suai.diplom.services.PriceService

@RestController
class PriceController(
    private val priceService: PriceService
) : PriceApi {
    override fun getPricesTaxi(
        fromAddress: String,
        toAddress: String,
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): ResponseEntity<List<TaxiPriceResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(priceService.getCurrentPrices(
                fromAddress = fromAddress,
                toAddress = toAddress,
                longitudeFrom = longitudeFrom,
                latitudeFrom = latitudeFrom,
                longitudeTo = longitudeTo,
                latitudeTo = latitudeTo
            ))
    }

    override fun addOrderHistoryPrice(addOrderHistoryRequest: addOrderHistoryRequest, authentication: Authentication?): ResponseEntity<HttpStatus> {
        priceService.addOrderHistoryPrice(addOrderHistoryRequest, authentication)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}