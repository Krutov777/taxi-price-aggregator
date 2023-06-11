package ru.suai.diplom.dto.request

import com.google.gson.annotations.SerializedName

//data class Point(
//    val latitude: String,
//    val longitude: String,
//    val address: String
//)

data class PricesTaxovichkofRequest(
    @SerializedName("PriceType")
    val priceType: String,
    @SerializedName("City")
    val city: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("Time")
    val time: String,
    @SerializedName("TimeToArrive")
    val timeToArrive: Int,
    @SerializedName("IsCustomDate")
    val isCustomDate: Boolean,
    @SerializedName("Options")
    val options: String,
//    @SerializedName("Points")
//    val points: List<Point>
    @SerializedName("Points")
    val points: List<List<String>>
)
