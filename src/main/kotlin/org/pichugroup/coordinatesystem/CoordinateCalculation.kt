package org.pichugroup.coordinatesystem

import kotlin.math.cos

internal fun distanceKMToLatitude(distanceKM: Double): Double {
    return distanceKM/ 111.574
}

internal fun distanceKMToLongitude(distanceKM: Double, latitude: Double): Double {
    return distanceKM / (111.320 * cos(latitude))
}