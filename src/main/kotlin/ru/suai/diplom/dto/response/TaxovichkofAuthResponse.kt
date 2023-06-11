package ru.suai.diplom.dto.response

import com.google.gson.annotations.SerializedName

data class TaxovichkofData(
    @SerializedName("Hash")
    val hash: String,
    @SerializedName("User")
    val user: String?,
    @SerializedName("Cards")
    val cards: String?,
    @SerializedName("Time")
    val time: String,
)

data class TaxovichkofAuthResponse(
    @SerializedName("Status")
    val status: Int,
    @SerializedName("ErrorText")
    val errorText: String,
    @SerializedName("Count")
    val count: Int,
    @SerializedName("Data")
    val taxovichkofData: TaxovichkofData
)
