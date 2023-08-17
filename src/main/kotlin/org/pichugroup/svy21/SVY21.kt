package org.pichugroup.svy21

import kotlin.math.*

data class LatLonCoordinate(
    val latitude: Double,
    val longitude: Double,
)

data class SVY21Coordinate(
    val easting: Double,
    val northing: Double,
)

internal class SVY21 {
    companion object {
        const val DEGREE_TO_RADIANS_FACTOR: Double = PI / 180

        // Datum and Projection, source: https://app.sla.gov.sg/sirent/About/PlaneCoordinateSystem
        const val SEMI_MAJOR_AXIS: Double = 6378137.0 // semi major axis of reference ellipsoid
        const val FLATTENING_RATIO: Double = 1 / 298.257223563 // ellipsoidal flattening
        const val ORIGIN_LATITUDE: Double = 1.366666
        const val ORIGIN_LONGITUDE: Double = 103.833333
        const val FALSE_NORTHING: Double = 38744.572
        const val FALSE_EASTING: Double = 28001.642
        const val SCALE_FACTOR: Double = 1.0 // central meridian scale factor
    }

    // Computed Projection Constants

    // Computed constants with powers (trailing number is the power)
    private var semiMinorAxis: Double = 0.0
    private var squaredEccentricity: Double = 0.0
    private var fourthPowerEccentricity: Double = 0.0
    private var sixthPowerEccentricity: Double = 0.0
    private var n: Double = 0.0
    private var n2: Double = 0.0
    private var n3: Double = 0.0
    private var n4: Double = 0.0
    private var gConstant: Double = 0.0

    // Expression Terms (trailing number is not the power)
    private var termA0: Double = 0.0
    private var termA2: Double = 0.0
    private var termA4: Double = 0.0
    private var termA6: Double = 0.0

    init {
        semiMinorAxis = SEMI_MAJOR_AXIS * (1 - FLATTENING_RATIO) // semi minor axis of reference ellipsoid

        squaredEccentricity =
            (2 * FLATTENING_RATIO) - (FLATTENING_RATIO.pow(2)) // squared eccentricity of reference ellipsoid
        fourthPowerEccentricity = squaredEccentricity.pow(2)
        sixthPowerEccentricity = fourthPowerEccentricity * squaredEccentricity

        termA0 = 1 - squaredEccentricity / 4 - 3 * fourthPowerEccentricity / 64 - 5 * sixthPowerEccentricity / 256
        termA2 = 3.0 / 8.0 * (squaredEccentricity + fourthPowerEccentricity / 4 + 15 * sixthPowerEccentricity / 128)
        termA4 = 15.0 / 256.0 * (fourthPowerEccentricity + 3 * sixthPowerEccentricity / 4)
        termA6 = 35 * sixthPowerEccentricity / 3072

        n = (SEMI_MAJOR_AXIS - semiMinorAxis) / (SEMI_MAJOR_AXIS + semiMinorAxis)
        n2 = n * n
        n3 = n2 * n
        n4 = n2 * n2

        gConstant =
            SEMI_MAJOR_AXIS * (1 - n) * (1 - n2) * (1 + (9 * n2 / 4) + (225 * n4 / 64)) * DEGREE_TO_RADIANS_FACTOR
    }

    private fun calculateMeridianDistance(latitude: Double): Double {
        val latitudeRadians: Double = latitude * DEGREE_TO_RADIANS_FACTOR
        return SEMI_MAJOR_AXIS * ((termA0 * latitudeRadians) - (termA2 * sin(2 * latitudeRadians)) + (termA4 * sin(4 * latitudeRadians)) - (termA6 * sin(
            6 * latitudeRadians
        )))
    }

    private fun calculateMeridianRadiusOfCurvature(latitudeSinSquared: Double): Double {
        val numerator: Double = SEMI_MAJOR_AXIS * (1 - squaredEccentricity)
        val denominator: Double = (1 - squaredEccentricity * latitudeSinSquared).pow(3.0 / 2.0)
        return numerator / denominator
    }

    private fun calculatePrimeVerticalRadiusOfCurvature(latitudeSinSquared: Double): Double {
        val polynomialTerm: Double = 1 - squaredEccentricity * latitudeSinSquared
        return SEMI_MAJOR_AXIS / sqrt(polynomialTerm)
    }

    fun convertSVY21ToLatLon(svy21Northing: Double, svy21Easting: Double): LatLonCoordinate {
        // naming conventions: similar to mathematics, I use prime to denote adjusted values
        val northingPrime: Double = svy21Northing - FALSE_NORTHING
        val originMeridianDistance: Double = calculateMeridianDistance(ORIGIN_LATITUDE)
        val meridianDistancePrime: Double = originMeridianDistance + (northingPrime / SCALE_FACTOR)
        val meridianDistance: Double = (meridianDistancePrime / gConstant) * DEGREE_TO_RADIANS_FACTOR

        // compute expression terms
        val latPrimeT1: Double = (3 * n / 2 - 27 * n3 / 32) * sin(2 * meridianDistance)
        val latPrimeT2: Double = (21 * n2 / 16 - 55 * n4 / 32) * sin(4 * meridianDistance)
        val latPrimeT3: Double = 151 * n3 / 96 * sin(6 * meridianDistance)
        val latPrimeT4: Double = 1097 * n4 / 512 * sin(8 * meridianDistance)
        val latitudePrime: Double = meridianDistance + latPrimeT1 + latPrimeT2 + latPrimeT3 + latPrimeT4

        val latitudeSinPrime: Double = sin(latitudePrime)
        val latitudeSinSquaredPrime: Double = latitudeSinPrime.pow(2)

        // trailing numbers are powers
        val meridianRadiusOfCurvaturePrime: Double = calculateMeridianRadiusOfCurvature(latitudeSinSquaredPrime)
        val primeVerticalRadiusOfCurvaturePrime: Double =
            calculatePrimeVerticalRadiusOfCurvature(latitudeSinSquaredPrime)
        val ratioPrimeVerticalMeridianRadiusOfCurvature: Double =
            primeVerticalRadiusOfCurvaturePrime / meridianRadiusOfCurvaturePrime
        val squaredRatioPrimeVerticalMeridianRadiusOfCurvature: Double =
            ratioPrimeVerticalMeridianRadiusOfCurvature * ratioPrimeVerticalMeridianRadiusOfCurvature
        val cubedRatioPrimeVerticalMeridianRadiusOfCurvature: Double =
            squaredRatioPrimeVerticalMeridianRadiusOfCurvature * ratioPrimeVerticalMeridianRadiusOfCurvature
        val quadPrimeVerticalMeridianRadiusOfCurvature: Double =
            cubedRatioPrimeVerticalMeridianRadiusOfCurvature * ratioPrimeVerticalMeridianRadiusOfCurvature
        val latitudePrimeTangent: Double = tan(latitudePrime)
        val squaredLatitudePrimeTangent: Double = latitudePrimeTangent.pow(2)
        val quadLatitudePrimeTangent: Double = squaredLatitudePrimeTangent.pow(2)
        val sixthLatitudePrimeTangent: Double = quadLatitudePrimeTangent * squaredLatitudePrimeTangent
        val eastingPrime: Double = svy21Easting - FALSE_EASTING

        /*** x represents normalized easting difference divided by product of central meridian scale factor
         * and radius of curvature in prime vertical
         */
        val x: Double = eastingPrime / (SCALE_FACTOR * primeVerticalRadiusOfCurvaturePrime)
        val x3: Double = x.pow(3)
        val x5: Double = x.pow(5)
        val x7: Double = x.pow(7)

        // Compute Latitude
        // Naming convention: latTerm1..4 are terms in an expression, not powers.
        val latFactor: Double = latitudePrimeTangent / (SCALE_FACTOR * meridianRadiusOfCurvaturePrime)
        val latTerm1: Double = latFactor * (eastingPrime * x / 2)
        val latTerm2: Double =
            latFactor * (eastingPrime * x3 / 24) * (-4 * squaredRatioPrimeVerticalMeridianRadiusOfCurvature + 9 * ratioPrimeVerticalMeridianRadiusOfCurvature * (1 - squaredLatitudePrimeTangent) + 12 * squaredLatitudePrimeTangent)
        val latTerm3: Double =
            latFactor * (eastingPrime * x5 / 720) * (8 * quadPrimeVerticalMeridianRadiusOfCurvature * (11 - 24 * squaredLatitudePrimeTangent) - 12 * cubedRatioPrimeVerticalMeridianRadiusOfCurvature * (21 - 71 * squaredLatitudePrimeTangent) + 15 * squaredRatioPrimeVerticalMeridianRadiusOfCurvature * (15 - 98 * squaredLatitudePrimeTangent + 15 * quadLatitudePrimeTangent) + 180 * ratioPrimeVerticalMeridianRadiusOfCurvature * (5 * squaredLatitudePrimeTangent - 3 * quadLatitudePrimeTangent) + 360 * quadLatitudePrimeTangent)
        val latTerm4: Double =
            latFactor * (eastingPrime * x7 / 40320) * (1385 - 3633 * squaredLatitudePrimeTangent + 4095 * quadLatitudePrimeTangent + 1575 * sixthLatitudePrimeTangent)
        val latitudeRadians: Double = latitudePrime - latTerm1 + latTerm2 - latTerm3 + latTerm4

        // Compute Longitude
        // Naming convention: lonTerm1..4 are terms in an expression, not powers.
        val latitudeSecant: Double = 1.0 / cos(latitudeRadians)
        val lonTerm1: Double = x * latitudeSecant
        val lonTerm2: Double =
            x3 * latitudeSecant / 6 * (ratioPrimeVerticalMeridianRadiusOfCurvature + 2 * squaredLatitudePrimeTangent)
        val lonTerm3: Double =
            x5 * latitudeSecant / 120 * (-4 * cubedRatioPrimeVerticalMeridianRadiusOfCurvature * (1 - 6 * squaredLatitudePrimeTangent) + squaredRatioPrimeVerticalMeridianRadiusOfCurvature * (9 - 68 * squaredLatitudePrimeTangent) + 72 * ratioPrimeVerticalMeridianRadiusOfCurvature * squaredLatitudePrimeTangent + 24 * quadLatitudePrimeTangent)
        val lonTerm4: Double =
            x7 * latitudeSecant / 5040 * (61 + 662 * squaredLatitudePrimeTangent + 1320 * quadLatitudePrimeTangent + 720 * sixthLatitudePrimeTangent)
        val longitudeRadians: Double =
            ORIGIN_LONGITUDE * DEGREE_TO_RADIANS_FACTOR + lonTerm1 - lonTerm2 + lonTerm3 - lonTerm4

        val latitudeDegrees: Double = latitudeRadians / DEGREE_TO_RADIANS_FACTOR
        val longitudeDegrees: Double = longitudeRadians / DEGREE_TO_RADIANS_FACTOR

        return LatLonCoordinate(latitudeDegrees, longitudeDegrees)
    }

}