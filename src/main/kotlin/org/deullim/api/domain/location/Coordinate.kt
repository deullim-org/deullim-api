package org.deullim.api.domain.location

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Embeddable
data class Coordinate(
    @Column(nullable = false)
    val latitude: Double,
    @Column(nullable = false)
    val longitude: Double,
) {
    init {
        require(latitude in MIN_LATITUDE..MAX_LATITUDE) {
            "Latitude must be in [$MIN_LATITUDE, $MAX_LATITUDE], but was $latitude"
        }
        require(longitude in MIN_LONGITUDE..MAX_LONGITUDE) {
            "Longitude must be in [$MIN_LONGITUDE, $MAX_LONGITUDE], but was $longitude"
        }
    }

    fun distanceTo(other: Coordinate): Double {
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val deltaLatRad = Math.toRadians(other.latitude - latitude)
        val deltaLonRad = Math.toRadians(other.longitude - longitude)

        val a =
            sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2) * sin(deltaLonRad / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000.0
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
    }
}
