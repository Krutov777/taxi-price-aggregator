package ru.suai.diplom.services.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import ru.suai.diplom.dto.request.addOrderHistoryRequest
import ru.suai.diplom.dto.response.HistoryPrice
import ru.suai.diplom.dto.response.HistoryPriceResponse
import ru.suai.diplom.dto.response.TaxiPriceResponse
import ru.suai.diplom.dto.response.TaxiPricesResponse
import ru.suai.diplom.exceptions.UserNotFoundException
import ru.suai.diplom.models.*
import ru.suai.diplom.repositories.*
import ru.suai.diplom.security.details.UserDetails
import ru.suai.diplom.services.PriceService
import ru.suai.diplom.services.clients.PriceClient
import ru.suai.diplom.utils.constants.GlobalConstants
import java.time.Duration
import java.util.*
import javax.transaction.Transactional


@Service
class PriceServiceImpl(
    private val priceRepository: PriceRepository,
    private val routeRepository: RouteRepository,
    private val orderPriceRepository: OrderPriceRepository,
    private val orderHistoryRepository: OrderHistoryRepository,
    private val taxiRepository: TaxiRepository,
    private val userRepository: UserRepository,
    private val priceClient: PriceClient,
    @Value("\${default-page-size}") private val defaultPageSize: Int
) : PriceService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun getCurrentPrices(
        fromAddress: String,
        toAddress: String,
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double
    ): TaxiPricesResponse = runBlocking {
        val routeList: List<Route> = routeRepository.findAll(
            fromLatitude = latitudeFrom,
            fromLongitude = longitudeFrom,
            toLatitude = latitudeTo,
            toLongitude = longitudeTo
        )
        val route = if (routeList.isEmpty()) {
            routeRepository.save(
                Route(
                    fromAddress = fromAddress,
                    toAddress = toAddress,
                    fromLatitude = latitudeFrom,
                    fromLongitude = longitudeFrom,
                    toLongitude = longitudeTo,
                    toLatitude = latitudeTo,
                )
            )
        } else routeList[0]

        val taxinfResponse: List<TaxiPriceResponse> = priceClient.getTaxiPricesAsync(
            latitudeFromParam = latitudeFrom,
            longitudeFromParam = longitudeFrom,
            latitudeBeforeParam = latitudeTo,
            longitudeBeforeParam = longitudeTo
        )
        val orderPrice = OrderPrice(
            dateTime = taxinfResponse[0].dateTime,
            route = route
        )
        orderPriceRepository.save(orderPrice)
        for (taxinfElem in taxinfResponse) {
            if (taxiRepository.findByName(taxinfElem.nameTaxi ?: "") == null)
                taxiRepository.save(Taxi(name = taxinfElem.nameTaxi))
            if (taxinfElem.price != null && taxinfElem.nameTaxi != null) {
                priceRepository.save(
                    Price(
                        dateTime = taxinfResponse[0].dateTime,
                        route = route,
                        taxi = taxiRepository.findByName(taxinfElem.nameTaxi ?: "") ?: Taxi(),
                        orderPrice = orderPrice,
                        price = taxinfElem.price ?: 0.0,
                        currency = taxinfElem.currency
                    )
                )
            }
        }
        return@runBlocking TaxiPricesResponse(taxinfResponse)
    }

    @Transactional
    override fun addOrderHistoryPrice(
        addOrderHistoryRequest: addOrderHistoryRequest,
        authentication: Authentication?
    ): Unit = runBlocking {
        if (authentication == null) {
            throw object : AuthenticationException(GlobalConstants.UNAUTHORIZED) {}
        } else {
            val userDetails = authentication.principal as? UserDetails
            userDetails?.username?.let {
                userRepository.findByEmail(it) ?: throw UserNotFoundException(GlobalConstants.USER_NOT_FOUND)
            }
                ?.let {
                    val routeList: List<Route> = routeRepository.findAll(
                        fromLatitude = addOrderHistoryRequest.latitudeFrom!!,
                        fromLongitude = addOrderHistoryRequest.longitudeFrom!!,
                        toLatitude = addOrderHistoryRequest.latitudeTo!!,
                        toLongitude = addOrderHistoryRequest.longitudeTo!!
                    )
                    val route = if (routeList.isEmpty()) {
                        routeRepository.save(
                            Route(
                                fromAddress = addOrderHistoryRequest.fromAddress,
                                toAddress = addOrderHistoryRequest.toAddress,
                                fromLatitude = addOrderHistoryRequest.latitudeFrom,
                                fromLongitude = addOrderHistoryRequest.longitudeFrom,
                                toLongitude = addOrderHistoryRequest.longitudeTo,
                                toLatitude = addOrderHistoryRequest.latitudeTo,
                            )
                        )
                    } else routeList[0]

                    val taxinfResponse: List<TaxiPriceResponse> = priceClient.getTaxiPricesAsync(
                        latitudeFromParam = addOrderHistoryRequest.latitudeFrom!!,
                        longitudeFromParam = addOrderHistoryRequest.longitudeFrom!!,
                        latitudeBeforeParam = addOrderHistoryRequest.latitudeTo!!,
                        longitudeBeforeParam = addOrderHistoryRequest.longitudeTo!!
                    )
                    val orderHistory = OrderHistory(
                        dateTime = taxinfResponse[0].dateTime,
                        route = route,
                        user = it,
                        status = OrderHistory.Status.IN_WORK
                    )
                    orderHistoryRepository.save(orderHistory)
                    for (taxinfElem in taxinfResponse) {
                        if (taxiRepository.findByName(taxinfElem.nameTaxi ?: "") == null)
                            taxiRepository.save(Taxi(name = taxinfElem.nameTaxi))
                        if (taxinfElem.price != null && taxinfElem.nameTaxi != null) {
                            priceRepository.save(
                                Price(
                                    dateTime = taxinfResponse[0].dateTime,
                                    route = route,
                                    taxi = taxiRepository.findByName(taxinfElem.nameTaxi ?: "") ?: Taxi(),
                                    price = taxinfElem.price ?: 0.0,
                                    currency = taxinfElem.currency
                                )
                            )
                        }
                    }
                }
        }
    }

    override fun getHistoryPrice(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeTo: Double,
        latitudeTo: Double,
//        page: Int
    ): HistoryPriceResponse {
        val routeList: List<Route> = routeRepository.findAll(
            fromLatitude = latitudeFrom,
            fromLongitude = longitudeFrom,
            toLatitude = latitudeTo,
            toLongitude = longitudeTo
        )
        return if (routeList.isNotEmpty()) {
            val listHistoryPrice = mutableListOf<HistoryPrice>()
            for (route in routeList) {
//                val pageRequest = PageRequest.of(page, defaultPageSize)
//                val priceHistory = priceRepository.findByRoute(route, pageRequest)
                val priceHistory = priceRepository.findByRoute(route)
                if (priceHistory.isEmpty()) {
                    return HistoryPriceResponse(listOf())
                }
                val priceHistoryTaxiPriceResponse: List<TaxiPriceResponse> = priceHistory.map {
                    TaxiPriceResponse(
                        nameTaxi = it.taxi.name,
                        price = it.price,
                        currency = it.currency,
                        dateTime = it.dateTime,
                        dateTimeNumber = it.dateTime?.time
                    )
                }
                listHistoryPrice.add(
                    HistoryPrice(
                        prices = priceHistoryTaxiPriceResponse,
                        fromAddress = route.fromAddress ?: "",
                        toAddress = route.toAddress ?: "",
                        fromLongitude = route.fromLongitude ?: 0.0,
                        fromLatitude = route.fromLatitude ?: 0.0,
                        toLongitude = route.toLongitude ?: 0.0,
                        toLatitude = route.toLatitude ?: 0.0
                    )
                )
            }
            return HistoryPriceResponse(listHistoryPrice)
        } else HistoryPriceResponse(listOf())
    }

    override fun getHistoryPrice(
        authentication: Authentication?,
//        page: Int
    ): HistoryPriceResponse {
        if (authentication == null) {
            throw object : AuthenticationException(GlobalConstants.UNAUTHORIZED) {}
        } else {
            val userDetails = authentication.principal as? UserDetails
            userDetails?.username?.let {
                userRepository.findByEmail(it) ?: throw UserNotFoundException(GlobalConstants.USER_NOT_FOUND)
            }
                ?.let { it ->
                    val routeList: List<Route> = orderHistoryRepository.findAll(userId = it.id ?: 0).distinctBy { it.route.id }.map { it.route }
                    return if (routeList.isNotEmpty()) {
                        val listHistoryPrice = mutableListOf<HistoryPrice>()
                        for (route in routeList) {
//                            val pageRequest = PageRequest.of(page, defaultPageSize)
//                            val priceHistory = priceRepository.findByRoute(route, pageRequest)
                            val priceHistory = priceRepository.findByRoute(route)
                            if (priceHistory.isEmpty())
                                return HistoryPriceResponse(listOf())
                            val priceHistoryTaxiPriceResponse: List<TaxiPriceResponse> = priceHistory.map {
                                TaxiPriceResponse(
                                    nameTaxi = it.taxi.name,
                                    price = it.price,
                                    currency = it.currency,
                                    dateTime = it.dateTime,
                                    dateTimeNumber = it.dateTime?.time
                                )
                            }
                            listHistoryPrice.add(
                                HistoryPrice(
                                    prices = priceHistoryTaxiPriceResponse,
                                    fromAddress = route.fromAddress ?: "",
                                    toAddress = route.toAddress ?: "",
                                    fromLongitude = route.fromLongitude ?: 0.0,
                                    fromLatitude = route.fromLatitude ?: 0.0,
                                    toLongitude = route.toLongitude ?: 0.0,
                                    toLatitude = route.toLatitude ?: 0.0
                                )
                            )
                        }
                        return HistoryPriceResponse(listHistoryPrice)

                    } else HistoryPriceResponse(listOf())
                }
            return HistoryPriceResponse(listOf())
        }
    }


//    single threaded
//    @Scheduled(fixedDelay = 30000)
//    fun countHistoryPrice() = runBlocking {
//        val orderHistory = orderHistoryRepository.findAll(OrderHistory.Status.IN_WORK.toString())
//        val currentDateTime = Date()
//        for (itemOrderHistory in orderHistory) {
////            delay(10000)
//            val taxinfResponse = priceClient.getTaxiPrices(
//                latitudeFromParam = itemOrderHistory.route.fromLatitude ?: continue,
//                longitudeFromParam = itemOrderHistory.route.fromLongitude ?: continue,
//                latitudeBeforeParam = itemOrderHistory.route.toLatitude ?: continue,
//                longitudeBeforeParam = itemOrderHistory.route.toLongitude ?: continue
//            )
//
//            for (taxinfElem in taxinfResponse) {
//                if (taxinfElem.price != null && taxinfElem.nameTaxi != null) {
//                    priceRepository.save(
//                        Price(
//                            dateTime = taxinfResponse[0].dateTime,
//                            route = itemOrderHistory.route,
//                            taxi = taxiRepository.findByName(taxinfElem.nameTaxi ?: "") ?: Taxi(),
//                            price = taxinfElem.price ?: 0.0,
//                            currency = taxinfElem.currency
//                        )
//                    )
//                }
//            }
//
//            val instantOrder = itemOrderHistory.dateTime?.toInstant()
//            val dayOrder = instantOrder?.atZone(ZoneId.systemDefault())?.dayOfMonth ?: continue
//            val timeOrder = instantOrder.atZone(ZoneId.systemDefault())?.toLocalTime() ?: continue
//
//            val currentInstant = currentDateTime.toInstant()
//            val currentDay = currentInstant?.atZone(ZoneId.systemDefault())?.dayOfMonth ?: continue
//            val currentTime = currentInstant.atZone(ZoneId.systemDefault())?.toLocalTime() ?: continue
//            if ((dayOrder.plus(2) <= currentDay)
//                || ((dayOrder.plus(1) == currentDay && timeOrder <= currentTime))
//            ) {
//                orderHistoryRepository.delete(itemOrderHistory)
//                itemOrderHistory.status = OrderHistory.Status.COMPLETED
//                orderHistoryRepository.save(itemOrderHistory)
//            }
//        }
//    }
//}


    @Scheduled(fixedDelay = 60 * 60 * 1000) // each hour
    fun countHistoryPrice() = runBlocking {
        val scope = CoroutineScope(SupervisorJob())
        val channel: Channel<Price?> = Channel()
        val orderHistory = orderHistoryRepository.findAll(OrderHistory.Status.IN_WORK.toString()).distinctBy { it.route.id }
        if (orderHistory.isEmpty())
            return@runBlocking
        val currentDateTime = Date()
        val job = scope.launch {
            val priceList = mutableListOf<Price?>()
            channel.consumeEach {
                priceList.add(it)
                if (orderHistory.size * 5 == priceList.size)
                    channel.close()
            }
            priceRepository.saveAll(priceList.mapNotNull { it })
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            scope.launch {
                repeat(5) {
                    channel.send(null)
                }
            }
            logger.warn(exception.message)
        }

        for (itemOrderHistory in orderHistory) {
            scope.launch(handler) {
                val taxinfResponse = priceClient.getTaxiPricesAsync(
                    latitudeFromParam = itemOrderHistory.route.fromLatitude ?: return@launch,
                    longitudeFromParam = itemOrderHistory.route.fromLongitude ?: return@launch,
                    latitudeBeforeParam = itemOrderHistory.route.toLatitude ?: return@launch,
                    longitudeBeforeParam = itemOrderHistory.route.toLongitude ?: return@launch
                )
                for (taxinfElem in taxinfResponse) {
                    if (taxinfElem.price != null && taxinfElem.nameTaxi != null) {
                        channel.send(
                            Price(
                                dateTime = taxinfResponse[0].dateTime,
                                route = itemOrderHistory.route,
                                taxi = taxiRepository.findByName(taxinfElem.nameTaxi ?: "") ?: Taxi(),
                                price = taxinfElem.price ?: 0.0,
                                currency = taxinfElem.currency
                            )
                        )
                    }
                }
                for(i in 1..5 - taxinfResponse.size) {
                    channel.send(null)
                }
            }
        }
        job.join()
        for (item in orderHistory) {
            val orderInstantDateTime = item.dateTime!!.toInstant()
            val currentInstantDateTime = currentDateTime.toInstant()

            val  deltaDuration: Duration = Duration.between(orderInstantDateTime, currentInstantDateTime)
            if (deltaDuration.toDays() >= 1L || deltaDuration.toDays() <= -1L) {
                item.status = OrderHistory.Status.COMPLETED
                orderHistoryRepository.save(item)
                // The two instants differ by exactly 1 day
            }
        }
    }
}
