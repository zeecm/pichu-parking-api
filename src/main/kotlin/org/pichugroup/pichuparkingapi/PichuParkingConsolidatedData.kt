package org.pichugroup.pichuparkingapi

import com.google.gson.annotations.SerializedName

data class PichuParkingAPIResponse(
    @SerializedName("Timestamp") var timestamp: String,
    @SerializedName("Data") var data: List<PichuParkingData>,
)

data class PichuParkingData(
    @SerializedName("CarparkID") var carparkID: String,
    @SerializedName("Latitude") var latitude: Float,
    @SerializedName("Longitude") var longitude: Float,
    @SerializedName("VehicleType") var vehicleType: String,
    @SerializedName("AvailableLots") var availableLots: Int,
    @SerializedName("Agency") var agency: String,
    @SerializedName("LocationName") var locationName: String,
)

