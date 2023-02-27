package ru.suai.diplom.services.impl

import org.springframework.stereotype.Service
import ru.suai.diplom.dto.response.TaxiPriceResponse
import ru.suai.diplom.models.OrderPrice
import ru.suai.diplom.models.Price
import ru.suai.diplom.models.Route
import ru.suai.diplom.models.Taxi
import ru.suai.diplom.repositories.OrderPriceRepository
import ru.suai.diplom.repositories.PriceRepository
import ru.suai.diplom.repositories.RouteRepository
import ru.suai.diplom.repositories.TaxiRepository
import ru.suai.diplom.services.PriceService
import ru.suai.diplom.services.clients.PriceClient
import java.util.*
import javax.transaction.Transactional

@Service
class PriceServiceImpl(
    private val priceRepository: PriceRepository,
    private val routeRepository: RouteRepository,
    private val orderPriceRepository: OrderPriceRepository,
    private val taxiRepository: TaxiRepository,
    private val priceClient: PriceClient
) : PriceService {
    @Transactional
    override fun getCurrentPrices(
        fromAddress: String,
        toAddress: String,
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): List<TaxiPriceResponse> {
        val routeList: List<Route> = routeRepository.findAll(
            fromLatitude = latitudeFrom,
            fromLongitude = longitudeFrom,
            toLatitude = latitudeTo,
            toLongitude = longitudeTo
        )
        val route = if (routeList.isEmpty()) {
            routeRepository.save(Route(
                fromAddress = fromAddress,
                toAddress = toAddress,
                fromLatitude = latitudeFrom,
                fromLongitude = longitudeFrom,
                toLongitude = longitudeTo,
                toLatitude = latitudeTo,
            ))
        } else routeList[0]
        val taxinfResponse: MutableList<TaxiPriceResponse> = priceClient.getTaxiPrices(
            latitudeFromParam = latitudeFrom,
            longitudeFromParam = longitudeFrom,
            latitudeBeforeParam = latitudeTo,
            longitudeBeforeParam = longitudeTo
        )

        val orderPrice = OrderPrice(
            dateTime = Date(),
            route = route
        )
        orderPriceRepository.save(orderPrice)
        for (taxinfElem in taxinfResponse) {
            if (taxiRepository.findByName(taxinfElem.nameTaxi ?: "") == null) {
                taxiRepository.save(
                    Taxi(
                        name = taxinfElem.nameTaxi
                    )
                )
            }
            if (taxinfElem.price != null && taxinfElem.nameTaxi != null) {
                priceRepository.save(
                    Price(
                        dateTime = Date(),
                        route = route,
                        taxi = taxiRepository.findByName(taxinfElem.nameTaxi ?: "") ?: Taxi(),
                        orderPrice = orderPrice,
                        price = taxinfElem.price ?: 0.0
                    )
                )
            }
        }
        return taxinfResponse
    }
}