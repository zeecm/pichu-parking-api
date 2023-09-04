package org.pichugroup.schema

import com.google.gson.annotations.SerializedName

data class PichuParkingAPIResponse(
    @SerializedName("timestamp") var timestamp: String,
    @SerializedName("data") var data: Set<PichuParkingData>,
)

sealed class PichuParkingData {
    abstract val carparkID: String
    abstract val carparkName: String
    abstract val vehicleCategory: String
    abstract val latitude: Double
    abstract val longitude: Double
}

data class PichuParkingLots(
    @SerializedName("carparkID") override val carparkID: String,
    @SerializedName("carparkName") override val carparkName: String,
    @SerializedName("latitude") override val latitude: Double,
    @SerializedName("longitude") override val longitude: Double,
    @SerializedName("vehicleCategory") override val vehicleCategory: String,
    @SerializedName("availableLots") val availableLots: Int,
    @SerializedName("agency") val agency: String
) : PichuParkingData()

data class PichuParkingRates(
    @SerializedName("carparkID") override val carparkID: String,
    @SerializedName("carparkName") override val carparkName: String,
    @SerializedName("latitude") override val latitude: Double,
    @SerializedName("longitude") override val longitude: Double,
    @SerializedName("vehicleCategory") override val vehicleCategory: String,
    @SerializedName("parkingSystem") val parkingSystem: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("timeRange") val timeRange: String,
    @SerializedName("weekdayMin") val weekdayMin: String,
    @SerializedName("weekdayRate") val weekdayRate: String,
    @SerializedName("saturdayMin") val saturdayMin: String,
    @SerializedName("saturdayRate") val saturdayRate: String,
    @SerializedName("sundayPHMin") val sundayPHMin: String,
    @SerializedName("sundayPHRate") val sundayPHRate: String
) : PichuParkingData()

