package ru.suai.diplom.dto.response

import com.google.gson.annotations.SerializedName


data class OptionsYandexResponse(
    val price: Double,
    @SerializedName("min_price")
    val minPrice: Double,
    @SerializedName("waiting_time")
    val waitingTime: Double,
    @SerializedName("class_name")
    val className: String,
    @SerializedName("class_text")
    val classText: String,
    @SerializedName("class_level")
    val classLevel: Int,
    @SerializedName("price_text")
    val priceText: String
)

data class YandexResponse(
    val options: List<OptionsYandexResponse>,
    val currency: String,
    val distance: Double,
    val time: Int,
    @SerializedName("time_text")
    val timeText: String
)
