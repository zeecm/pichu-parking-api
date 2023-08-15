package org.pichugroup.thirdpartyparkingapi

import org.pichugroup.schema.*

fun translateURAParkingLotResponse(uraParkingLotResponse: URAParkingLotResponse): Set<PichuParkingData> {
    if (uraParkingLotResponse.status != "Success") {
        throw Exception("API Returned Invalid Data")
    }

    val parkingLots: List<URAParkingLotData> = uraParkingLotResponse.result

    val finalList = mutableListOf<PichuParkingData>()

    for (lotData in parkingLots) {
        val pichuData = translateURAParkingLotData(lotData)
        finalList.add(pichuData)
    }

    return finalList.toSet()
}

private fun translateURAParkingLotData(uraParkingLotData: URAParkingLotData): PichuParkingData {
    val carparkID = uraParkingLotData.carparkNo
    val carparkName = ""
    val vehicleCategory = uraParkingLotData.lotType
    val availableLots = uraParkingLotData.lotsAvailable.toInt()
    val latitude = 0.0 // TODO: Convert SVY21
    val longitude = 0.0
    return PichuParkingData(
        carparkID = carparkID,
        carparkName = carparkName,
        vehicleCategory = vehicleCategory,
        availableLots = availableLots,
        latitude = latitude,
        longitude = longitude
    )
}

fun translateLTAParkingAvailabilityResponse(ltaParkingLotResponse: LTAParkingAvailabilityResponse): Set<PichuParkingData> {
    val parkingAvailability: List<LTAParkingAvailabilityData> = ltaParkingLotResponse.value

    val finalList = mutableListOf<PichuParkingData>()

    for (availabilityData in parkingAvailability) {
        val pichuData = translateLTAParkingAvailabilityData(availabilityData)
        finalList.add(pichuData)
    }
    return finalList.toSet()
}

private fun translateLTAParkingAvailabilityData(ltaParkingAvailabilityData: LTAParkingAvailabilityData): PichuParkingData {
    val carparkID = ltaParkingAvailabilityData.carparkID
    val carparkName = ltaParkingAvailabilityData.development
    val latitude = 0.0 // TODO: Convert Lat Long String
    val longitude = 0.0
    val vehicleCategory = ltaParkingAvailabilityData.lotType
    val availableLots = ltaParkingAvailabilityData.availableLots
    return PichuParkingData(
        carparkID = carparkID,
        carparkName = carparkName,
        vehicleCategory = vehicleCategory,
        availableLots = availableLots,
        latitude = latitude,
        longitude = longitude
    )
}