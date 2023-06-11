package ru.suai.diplom.dto.request

import com.google.gson.annotations.SerializedName

data class PricesCityMobilRequest(
    val method: String,
    val ver: String,
    @SerializedName("phone_os")
    val phoneOs: String,
    @SerializedName("os_version")
    val osVersion: String,
    val locale: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("del_latitude")
    val delLatitude: Double,
    @SerializedName("del_longitude")
    val delLongitude: Double,
    val options: List<Any>,
    @SerializedName("payment_type")
    val paymentType: List<String>,
    @SerializedName("tariff_group")
    val tariffGroup: List<Int>,
    val source: String,
    val hurry: Int,
    val captcha: String
)
