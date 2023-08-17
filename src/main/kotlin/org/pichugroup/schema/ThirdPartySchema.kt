package org.pichugroup.schema

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class URAParkingLotResponse(
    @SerializedName("Status") var status: String,
    @SerializedName("Message") var message: String,
    @SerializedName("Result") var result: List<URAParkingLotData>,
)

data class URAParkingLotData(
    @SerializedName("lotsAvailable") var lotsAvailable: String,
    @SerializedName("lotType") var lotType: String,
    @SerializedName("carparkNo") var carparkNo: String,
    @SerializedName("geometries") var geometries: List<URACoordinates>,
)

data class URACoordinates(
    @SerializedName("coordinates") var coordinates: String,
)

data class URAParkingRatesResponse(
    @SerializedName("Status") var status: String,
    @SerializedName("Message") var message: String,
    @SerializedName("Result") var result: List<URAParkingRatesData>,
)

data class URAParkingRatesData(
    @SerializedName("weekdayMin") var weekdayMin: String,
    @SerializedName("ppName") var ppName: String,
    @SerializedName("endTime") var endTime: String,
    @SerializedName("weekdayRate") var weekdayRate: String,
    @SerializedName("startTime") var startTime: String,
    @SerializedName("ppCode") var ppCode: String,
    @SerializedName("sunPHRate") var sunPHRate: String,
    @SerializedName("satdayMin") var satdayMin: String,
    @SerializedName("sunPHMin") var sunPHMin: String,
    @SerializedName("parkingSystem") var parkingSystem: String,
    @SerializedName("parkCapacity") var parkCapacity: String,
    @SerializedName("vehCat") var vehCat: String,
    @SerializedName("satdayRate") var satdayRate: String,
    @SerializedName("geometries") var geometries: List<URACoordinates>,
)

data class LTAParkingAvailabilityResponse(
    @SerializedName("odata.metadata") var metadata: String,
    @SerializedName("value") var value: List<LTAParkingAvailabilityData>,
)

data class LTAParkingAvailabilityData(
    @SerializedName("CarParkID") var carparkID: String,
    @SerializedName("Area") var area: String,
    @SerializedName("Development") var development: String,
    @SerializedName("Location") var location: String,
    @SerializedName("AvailableLots") var availableLots: Int,
    @SerializedName("LotType") var lotType: String,
    @SerializedName("Agency") var agency: String,
)

inline fun <reified T> deserializeJsonTextToSchema(jsonText: String): T {
    val gson = Gson()
    return gson.fromJson(jsonText, T::class.java)
}
