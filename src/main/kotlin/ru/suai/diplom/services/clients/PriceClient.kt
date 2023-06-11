package ru.suai.diplom.services.clients

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.use
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.suai.diplom.dto.request.GetTokenTaxovichkofRequest
import ru.suai.diplom.dto.request.PricesCityMobilRequest
import ru.suai.diplom.dto.request.PricesTaxovichkofRequest
import ru.suai.diplom.dto.response.*
import ru.suai.diplom.utils.constants.GlobalConstants
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_AUTH_TAXOVICHKOF
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_CITYMOBIL
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_TAXINF
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_TAXOVICHKOF
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_YANDEX
import ru.suai.diplom.utils.unescapeUnicode
import java.util.*


@Component
class PriceClient(
    @Value("\${clid}") private val clid: String,
    @Value("\${apikey}") private val apikey: String
) {
    private var urlYandex = "$BASE_URL_YANDEX?clid=$clid&apikey=$apikey"
    private val client = OkHttpClient()
    private val logger = LoggerFactory.getLogger(javaClass)
    private val gson = Gson()

    fun getPriceYandex(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double
    ): YandexResponse? {
        val urlYandexRequest = "$urlYandex&rll=$longitudeFrom,$latitudeFrom~$longitudeBefore,$latitudeBefore"
        val request = Request.Builder()
            .url(urlYandexRequest)
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return null.also { logger.info("Unexpected code $response") }
            logger.info(request.toString())
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty())
                return null.also { logger.info("successful request for Yandex taxi with empty body: $response") }
            return@use gson.fromJson(
                responseBody,
                YandexResponse::class.java
            ).also { logger.info("successful request for Yandex taxi:  $it") }
        }
    }

    fun getPriceCityMobil(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double,
        userAgent: String = GlobalConstants.userAgent[(0..999).random()]
    ): CityMobilResponse? {
        val pricesCityMobilRequest = PricesCityMobilRequest(
            method = "getprice",
            ver = "4.59.0",
            phoneOs = "widget",
            osVersion = "web mobile-web",
            locale = "ru",
            latitude = latitudeFrom,
            longitude = longitudeFrom,
            delLatitude = latitudeBefore,
            delLongitude = longitudeBefore,
            options = listOf(),
            paymentType = listOf("cash"),
            tariffGroup = listOf(
                2,
                4,
                13,
                7,
                5
            ),
            source = "O",
            hurry = 1,
            captcha = "xui"
        )
        val request = Request.Builder()
            .url(BASE_URL_CITYMOBIL)
            .header("Origin", "https://city-mobil.ru")
            .post(gson.toJson(pricesCityMobilRequest).toRequestBody())
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return null.also { logger.info("Unexpected code $response") }
            logger.info(request.toString())
            val body = response.body
            val responseBody = body?.string()
            if (responseBody.isNullOrEmpty())
                return null.also { logger.info("successful request for City Mobil taxi with empty body: $response") }

            return@use gson.fromJson(
                responseBody,
                CityMobilResponse::class.java
            ).also { logger.info("successful request for City Mobil taxi:  $it") }
        }
    }

    fun getPriceTaxovichkof(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double
    ): TaxovichkofResponse? {
        val getTokenTaxovichkofRequest = GetTokenTaxovichkofRequest(
            phone = null,
            password = null,
            key = "HkTmgKwcm9Te5vnH",
            slug = "tf_site"
        )

        val requestAuth = Request.Builder()
            .url(BASE_URL_AUTH_TAXOVICHKOF)
            .post(gson.toJson(getTokenTaxovichkofRequest).toRequestBody())
            .build()
        val taxovichkofAuthResponse: TaxovichkofAuthResponse = client.newCall(requestAuth).execute().use { response ->
            if (!response.isSuccessful)
                return null.also { logger.info("Unexpected code $response")}
            logger.info(requestAuth.toString())
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty())
                return null.also { logger.info("successful request for Taxovichkof taxi with empty body: $response")}
            return@use gson.fromJson(
                responseBody,
                TaxovichkofAuthResponse::class.java
            ).also { logger.info("successful request for Taxovichkof taxi:  $it") }
        }

        val pricesTaxovichkofRequest = PricesTaxovichkofRequest(
            priceType = "5",
            city = "0",
            date = "2023-06-09",
            time = "2341",
            timeToArrive = 10,
            isCustomDate = false,
            options = "0000000000000000000000000000000000001000000000",
            points = listOf(
                listOf(
                    latitudeFrom.toString(),
                    longitudeFrom.toString(),
                    "from"
                ),
                listOf(
                    latitudeBefore.toString(),
                    longitudeBefore.toString(),
                    "to"
                )
            )
        )

        val request = Request.Builder()
            .url(BASE_URL_TAXOVICHKOF)
            .header("Authorization", "Bearer ${taxovichkofAuthResponse.taxovichkofData.hash}")
            .post(gson.toJson(pricesTaxovichkofRequest).toRequestBody())
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return null.also { logger.info("Unexpected code $response")}
            logger.info(request.toString())
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty())
                return null.also { logger.info("successful request for Taxovichkof taxi with empty body: $response")}
            return@use gson.fromJson(
                responseBody,
                TaxovichkofResponse::class.java
            ).also { logger.info("successful request for Taxovichkof taxi:  $it") }
        }
    }


    fun getPriceOtherTaxi(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double,
        userAgent: String = GlobalConstants.userAgent[(0..999).random()]
    ): TaxinfResponse? {
        val urlTaxinf = "$BASE_URL_TAXINF&from_geo=$latitudeFrom,$longitudeFrom&to_geo=$latitudeBefore,$longitudeBefore"
        val request = Request.Builder()
            .url(urlTaxinf)
            .header("User-Agent", userAgent)
            .build()

        val response = client.newCall(request).execute().use { response ->
            if (response.code == 429)
                return getPriceOtherTaxi(
                    longitudeFrom,
                    latitudeFrom,
                    longitudeBefore,
                    latitudeBefore,
                    userAgent = GlobalConstants.userAgent[(0..999).random()]
                )
            if (!response.isSuccessful)
                return null.also { logger.warn("Unexpected code $response") }

            logger.info(request.toString())
            return@use gson.fromJson(
                response.body?.string()?.unescapeUnicode(),
                TaxinfResponse::class.java
            ).also { logger.info("successful request for Taxinf:  $it") }
        }
        return response
    }

    suspend fun getTaxiPricesAsync(
        longitudeFromParam: Double,
        latitudeFromParam: Double,
        longitudeBeforeParam: Double,
        latitudeBeforeParam: Double
    ): List<TaxiPriceResponse> = withContext(Dispatchers.IO) {
        val taxinfResponse: TaxinfResponse? = getPriceOtherTaxi(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )

        val yandexResponse: YandexResponse? = getPriceYandex(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )

        val cityMobilResponse: CityMobilResponse? = getPriceCityMobil(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )

        val taxovichkofResponse: TaxovichkofResponse? = getPriceTaxovichkof(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )

        val curDateTime = Date()
        val taxiPricesResponse: MutableList<TaxiPriceResponse> = mutableListOf()
        if (yandexResponse != null) {
            taxiPricesResponse.add(
                TaxiPriceResponse(
                    nameTaxi = "Яндекс",
                    price = yandexResponse.options[0].price,
                    currency = yandexResponse.currency,
                    dateTime = curDateTime,
                    dateTimeNumber = curDateTime.time
                )
            )
        }
        if (taxinfResponse != null) {
            for (taxinfElem in taxinfResponse.prices) {
                if (taxinfElem.name != "Яндекс") {
                    taxiPricesResponse.add(
                        TaxiPriceResponse(
                            nameTaxi = taxinfElem.name,
                            price = taxinfElem.price,
                            currency = if (taxinfElem.currency == "₽") "RUB" else taxinfElem.currency,
                            dateTime = curDateTime,
                            dateTimeNumber = curDateTime.time
                        )
                    )
                }
            }
        }
        if (cityMobilResponse != null) {
            taxiPricesResponse.add(
                TaxiPriceResponse(
                    nameTaxi = "Ситимобил",
                    price = cityMobilResponse.prices[0].price,
                    currency = "RUB",
                    dateTime = curDateTime,
                    dateTimeNumber = curDateTime.time
                )
            )
        }
        if (taxovichkofResponse != null) {
            taxiPricesResponse.add(
                TaxiPriceResponse(
                    nameTaxi = "Таксовичкоф",
                    price = taxovichkofResponse.data.pricesTaxovichkof["1"]?.oldPrice?.toDouble(),
                    currency = "RUB",
                    dateTime = curDateTime,
                    dateTimeNumber = curDateTime.time
                )
            )
        }
        return@withContext taxiPricesResponse
    }

    fun getTaxiPrices(
        longitudeFromParam: Double,
        latitudeFromParam: Double,
        longitudeBeforeParam: Double,
        latitudeBeforeParam: Double
    ): List<TaxiPriceResponse> {
        val taxinfResponse: TaxinfResponse? = getPriceOtherTaxi(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )

        val yandexResponse: YandexResponse? = getPriceYandex(
            longitudeFrom = longitudeFromParam,
            latitudeFrom = latitudeFromParam,
            longitudeBefore = longitudeBeforeParam,
            latitudeBefore = latitudeBeforeParam
        )
        val curDateTime = Date()
        val taxiPricesResponse: MutableList<TaxiPriceResponse> = mutableListOf()
        if (yandexResponse != null) {
            taxiPricesResponse.add(
                TaxiPriceResponse(
                    nameTaxi = "Яндекс",
                    price = yandexResponse.options[0].price,
                    currency = yandexResponse.currency,
                    dateTime = curDateTime,
                    dateTimeNumber = curDateTime.time
                )
            )
        }
        if (taxinfResponse != null) {
            for (taxinfElem in taxinfResponse.prices) {
                if (taxinfElem.name != "Яндекс") {
                    taxiPricesResponse.add(
                        TaxiPriceResponse(
                            nameTaxi = taxinfElem.name,
                            price = taxinfElem.price,
                            currency = if (taxinfElem.currency == "₽") "RUB" else taxinfElem.currency,
                            dateTime = curDateTime,
                            dateTimeNumber = curDateTime.time
                        )
                    )
                }
            }
        }
        return taxiPricesResponse
    }
}