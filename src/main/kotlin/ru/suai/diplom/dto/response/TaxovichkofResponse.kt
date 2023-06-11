package ru.suai.diplom.dto.response

import com.google.gson.annotations.SerializedName

data class TaxoTarif(
    val used: String
)

data class PriceItemTaxovichkof(
    @SerializedName("discount_price_by_proc")
    val discountPriceByProc: String,
    @SerializedName("discount_proc_used")
    val discountProcUsed: String,
    @SerializedName("driver_price")
    val driverPrice: String,
    @SerializedName("fake_hash")
    val fakeHash: String,
    @SerializedName("fake_price")
    val fakePrice: String,
    val hash: String,
    @SerializedName("hexo_proc_a_in")
    val hexoProcAIn: String,
    val id: String,
    @SerializedName("is_driver_price")
    val isDriverPrice: String,
    @SerializedName("k_jam")
    val kJam: String,
    @SerializedName("old_hash")
    val oldHash: String,
    @SerializedName("old_price")
    val oldPrice: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("proc_a_in")
    val procAin: String,
    @SerializedName("promo_discount_price")
    val promoDiscountPrice: String,
    @SerializedName("taxo_tarif")
    val taxoTarif: TaxoTarif,
    @SerializedName("taxo_to_fixed")
    val taxoToFixed: String,
)

data class PricesTaxovichkof(
    val prices: Map<String, PriceItemTaxovichkof>
)

data class RouteSummary(
    @SerializedName("total_time")
    val totalTime: Int,
    @SerializedName("total_distance")
    val totalDistance: Int,
)

data class DataTaxovichof(
    @SerializedName("control_distance")
    val controlDistance: String,
    @SerializedName("route_summary")
    val routeSummary: RouteSummary,
    @SerializedName("prices")
    val pricesTaxovichkof: Map<String, PriceItemTaxovichkof>,
    @SerializedName("route_geometry")
    val routeGeometry: String
)

data class TaxovichkofResponse(
    @SerializedName("Status")
    val status: Int,
    @SerializedName("ErrorText")
    val errorText: String,
    @SerializedName("Count")
    val count: Int,
    @SerializedName("Data")
    val data: DataTaxovichof,
)
