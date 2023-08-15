package org.pichugroup.schema

import com.google.gson.annotations.SerializedName

data class PichuParkingAPIResponse(
    @SerializedName("Timestamp") var timestamp: String,
    @SerializedName("Data") var data: List<PichuParkingData>,
)

data class PichuParkingData(
    @SerializedName("CarparkID") var carparkID: String,
    @SerializedName("CarparkName") var carparkName: String,
    @SerializedName("Latitude") var latitude: Double,
    @SerializedName("Longitude") var longitude: Double,
    @SerializedName("VehicleCategory") var vehicleCategory: String,
    @SerializedName("AvailableLots") var availableLots: Int,
)

data class PichuParkingRates(
    @SerializedName("CarparkID") var carparkID: String,
    @SerializedName("VehicleCategory") var vehicleCategory: String,
    @SerializedName("Latitude") var latitude: Double,
    @SerializedName("Longitude") var longitude: Double,
    @SerializedName("parkingSystem") var parkingSystem: String,
    @SerializedName("TimeRange") var timeRange: String,
    @SerializedName("WeekdayMin") var weekdayMin: String,
    @SerializedName("WeekdayRate") var weekdayRate: String,
    @SerializedName("SaturdayMin") var saturdayMin: String,
    @SerializedName("SaturdayRate") var saturdayRate: String,
    @SerializedName("SundayMin") var sundayMin: String,
    @SerializedName("SundayPHRate") var sundayPHRate: String,
)

