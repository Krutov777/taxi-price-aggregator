package ru.suai.diplom.dto.response

import com.google.gson.annotations.SerializedName

data class CityMobilPrice(
    val price: Double,
    @SerializedName("id_tariff_group")
    val idTariffGroup: String,
    @SerializedName("id_tariff")
    val idTariff: Int,
)

data class CityMobilResponse(
    @SerializedName("id_calculation")
    val idCalculation: String,
    val prices: List<CityMobilPrice>,
    @SerializedName("duration_text")
    val durationText: String,
    val duration: Double
)
