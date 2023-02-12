package ru.suai.diplom.dto.response

import com.google.gson.annotations.SerializedName

data class TaxinfParametrs(
    val id: Long,
    val name: String,
    val logotype: String,
    val price: Int,
    val currency: String,
    @SerializedName("button_text")
    val buttonText: String,
    @SerializedName("link_ios")
    val linkIos: String,
    @SerializedName("link_android")
    val linkAndroid: String,
    @SerializedName("type_link_android")
    val typeLinkAndroid: String,
    @SerializedName("type_link_ios")
    val typeLinkIos: String,
    @SerializedName("appstore_link")
    val appstoreLink: String,
    @SerializedName("google_play_link")
    val googlePlayLink: String,
    @SerializedName("promo_code_description")
    val promoCodeDescription: String,
    @SerializedName("promotional_code")
    val promotionalCode: String,
    @SerializedName("promo_value")
    val promoValue: String,
    @SerializedName("check_ios")
    val checkIos: String,
    @SerializedName("check_android")
    val checkAndroid: String,
    @SerializedName("show_if_not_installed_ios")
    val showIfNotInstalledIos: String,
    @SerializedName("show_if_not_installed_android")
    val showIfNotInstalledAndroid: String
)

data class TaxinfResponse(
    val prices: List<TaxinfParametrs>
)
