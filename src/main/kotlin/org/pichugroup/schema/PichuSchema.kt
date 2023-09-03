package org.pichugroup.schema

import com.google.gson.annotations.SerializedName

data class PichuParkingAPIResponse(
    @SerializedName("timestamp") var timestamp: String,
    @SerializedName("data") var data: Set<PichuParkingData>,
)

data class PichuParkingData(
    @SerializedName("carparkID") var carparkID: String,
    @SerializedName("carparkName") var carparkName: String,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("vehicleCategory") var vehicleCategory: String,
    @SerializedName("availableLots") var availableLots: Int,
    @SerializedName("agency") var agency: String,
)

data class PichuParkingRates(
    @SerializedName("carparkID") var carparkID: String,
    @SerializedName("carparkName") var carparkName: String,
    @SerializedName("vehicleCategory") var vehicleCategory: String,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("parkingSystem") var parkingSystem: String,
    @SerializedName("capacity") var capacity: Int,
    @SerializedName("timeRange") var timeRange: String,
    @SerializedName("weekdayMin") var weekdayMin: String,
    @SerializedName("weekdayRate") var weekdayRate: String,
    @SerializedName("saturdayMin") var saturdayMin: String,
    @SerializedName("saturdayRate") var saturdayRate: String,
    @SerializedName("sundayPHMin") var sundayPHMin: String,
    @SerializedName("sundayPHRate") var sundayPHRate: String,
)

