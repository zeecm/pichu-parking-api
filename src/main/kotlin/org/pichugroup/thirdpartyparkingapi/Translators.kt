package org.pichugroup.thirdpartyparkingapi

import org.pichugroup.schema.*
import org.pichugroup.svy21.LatLonCoordinate
import org.pichugroup.svy21.SVY21
import org.pichugroup.svy21.SVY21Coordinate

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
                longitude = longitude,
                agency = "URA"
            )
        )
    }
    return parkingData
}

fun translateURAParkingRatesResponse(uraParkingRatesResponse: URAParkingRatesResponse): Set<PichuParkingRates> {
    if (uraParkingRatesResponse.status != "Success") {
        throw Exception("API Returned Invalid Data")
    }

    val parkingRates: List<URAParkingRatesData> = uraParkingRatesResponse.result

    val finalList = mutableListOf<PichuParkingRates>()

    for (ratesData in parkingRates) {
        val pichuData: List<PichuParkingRates> = translateURAParkingRatesData(ratesData)
        finalList.addAll(pichuData)
    }

    return finalList.toSet()
}

private fun translateURAParkingRatesData(uraParkingRatesData: URAParkingRatesData): List<PichuParkingRates> {
    val svy21 = SVY21()

    val ratesData = mutableListOf<PichuParkingRates>()

    val carparkID = uraParkingRatesData.ppCode
    val carparkName = uraParkingRatesData.ppName
    val vehicleCategory = uraParkingRatesData.vehCat
    val parkingSystem = uraParkingRatesData.parkingSystem
    val capacity = uraParkingRatesData.parkCapacity
    val weekdayMin = uraParkingRatesData.weekdayMin
    val weekdayRate = uraParkingRatesData.weekdayRate
    val saturdayMin = uraParkingRatesData.satdayMin
    val saturdayRate = uraParkingRatesData.satdayRate
    val sundayPHMin = uraParkingRatesData.sunPHMin
    val sundayPHRate = uraParkingRatesData.sunPHRate
    val timeRange = "${uraParkingRatesData.startTime} to ${uraParkingRatesData.endTime}"

    for (coordinates in uraParkingRatesData.geometries) {
        val svy21Coordinate = splitURASvy21String(coordinates.coordinates)
        val latLonCoordinate: LatLonCoordinate =
            svy21.convertSVY21ToLatLon(svy21Easting = svy21Coordinate.easting, svy21Northing = svy21Coordinate.northing)
        val latitude: Double = latLonCoordinate.latitude
        val longitude: Double = latLonCoordinate.longitude
        ratesData.add(
            PichuParkingRates(
                carparkID,
                carparkName,
                vehicleCategory,
                latitude,
                longitude,
                parkingSystem,
                capacity,
                timeRange,
                weekdayMin,
                weekdayRate,
                saturdayMin,
                saturdayRate,
                sundayPHMin,
                sundayPHRate
            )
        )
    }
    return ratesData
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
    val agency = ltaParkingAvailabilityData.agency
    return PichuParkingData(
        carparkID = carparkID,
        carparkName = carparkName,
        vehicleCategory = vehicleCategory,
        availableLots = availableLots,
        latitude = latitude,
        longitude = longitude,
        agency = agency
    )
}

private fun splitLTACoordinateString(coordinateString: String): LatLonCoordinate {
    val coordinateList: List<Double> = coordinateString.split(" ").map { it.toDouble() }
    val (latitude: Double, longitude: Double) = coordinateList
    return LatLonCoordinate(latitude = latitude, longitude = longitude)
}