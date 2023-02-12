package ru.suai.diplom.services

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import okio.use
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

import org.springframework.stereotype.Component
import ru.suai.diplom.dto.response.TaxinfResponse
import ru.suai.diplom.dto.response.YandexResponse
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_TAXINF
import ru.suai.diplom.utils.constants.GlobalConstants.BASE_URL_YANDEX
import ru.suai.diplom.utils.unescapeUnicode
import javax.annotation.PostConstruct

@Component
class PriceClient(
    @Value("\${clid}") private val clid: String,
    @Value("\${apikey}") private val apikey: String
) {
    private var urlYandex = "$BASE_URL_YANDEX?clid=$clid&apikey=$apikey"
    private val client = OkHttpClient()
    private val logger = LoggerFactory.getLogger(javaClass)
    private val gson = Gson()

    @PostConstruct
    fun getPriceYandex(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double
    ): YandexResponse {
        urlYandex = "$urlYandex&rll=$longitudeFrom,$latitudeFrom~$longitudeBefore,$latitudeBefore"
        val request = Request.Builder()
            .url(urlYandex)
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.warn("Unexpected code $response")
                throw IOException("Unexpected code $response")
            }
            return@use gson.fromJson(
                response.body?.string(),
                YandexResponse::class.java
            ).also { logger.info("successful request for Yandex taxi:  $it") }
        }
    }

    @PostConstruct
    fun getPriceOtherTaxi(
        longitudeFrom: Double,
        latitudeFrom: Double,
        longitudeBefore: Double,
        latitudeBefore: Double
    ): TaxinfResponse {
        val urlTaxinf = "$BASE_URL_TAXINF&from_geo=$latitudeFrom,$longitudeFrom&to_geo=$latitudeBefore,$longitudeBefore"
        val request = Request.Builder()
            .url(urlTaxinf)
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.warn("Unexpected code $response")
                throw IOException("Unexpected code $response")
            }

            return@use gson.fromJson(
                response.body?.string()?.unescapeUnicode(),
                TaxinfResponse::class.java
            ).also { logger.info("successful request for Taxinf:  $it") }
        }
    }
}