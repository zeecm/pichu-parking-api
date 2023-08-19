package org.pichugroup.thirdpartyparkingapi

import org.pichugroup.schema.*
import org.pichugroup.coordinatesystem.LatLonCoordinate
import org.pichugroup.coordinatesystem.SVY21
import org.pichugroup.coordinatesystem.SVY21Coordinate

fun translateURAParkingLotResponse(uraParkingLotResponse: URAParkingLotResponse): Set<PichuParkingData> {
    if (uraParkingLotResponse.status != "Success") {
        throw Exception("API Returned Invalid Data")
    }

    val parkingLots: List<URAParkingLotData> = uraParkingLotResponse.result

    val finalList = mutableListOf<PichuParkingData>()

    for (lotData in parkingLots) {
        val pichuData: List<PichuParkingData> = translateURAParkingLotData(lotData)
        finalList.addAll(pichuData)
    }

    return finalList.toSet()
}

private fun translateURAParkingLotData(uraParkingLotData: URAParkingLotData): List<PichuParkingData> {
    val svy21 = SVY21()

    val parkingData = mutableListOf<PichuParkingData>()

    val carparkID = uraParkingLotData.carparkNo
    val carparkName = ""
    val vehicleCategory = uraParkingLotData.lotType
    val availableLots = uraParkingLotData.lotsAvailable.toInt()

    for (coordinates in uraParkingLotData.geometries) {
        val svy21Coordinate = splitURASvy21String(coordinates.coordinates)
        val latLonCoordinate: LatLonCoordinate =
            svy21.convertSVY21ToLatLon(svy21Easting = svy21Coordinate.easting, svy21Northing = svy21Coordinate.northing)
        val latitude: Double = latLonCoordinate.latitude
        val longitude: Double = latLonCoordinate.longitude
        parkingData.add(
            PichuParkingData(
                carparkID = carparkID,
                carparkName = carparkName,
                vehicleCategory = vehicleCategory,
                availableLots = availableLots,
                latitude = latitude,
                longitude = longitude
            )
        )
    }
    return parkingData
}

private fun splitURASvy21String(svy21String: String): SVY21Coordinate {
    val svy21Doubles: List<Double> = svy21String.split(",").map { it.toDouble() }
    val (svy21Easting: Double, svy21Northing: Double) = svy21Doubles
    return SVY21Coordinate(easting = svy21Easting, northing = svy21Northing)
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
    val latLonCoordinate: LatLonCoordinate = splitLTACoordinateString(ltaParkingAvailabilityData.location)
    val latitude = latLonCoordinate.latitude
    val longitude = latLonCoordinate.longitude
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

private fun splitLTACoordinateString(coordinateString: String): LatLonCoordinate {
    val coordinateList: List<Double> = coordinateString.split(" ").map { it.toDouble() }
    val (latitude: Double, longitude: Double) = coordinateList
    return LatLonCoordinate(latitude = latitude, longitude = longitude)
}