//Filepath: com/main/wheres_the_craic/util/distanceInKm.kt
package com.main.wheres_the_craic.util

import android.location.Location

/**
 * Calculates the distance in km between two LatLng points.
 * @param from The starting point.
 * @param to The ending point.
 * @return The distance in kilometers.
 */
fun distanceInKm(from: com.google.android.gms.maps.model.LatLng, to: com.google.android.gms.maps.model.LatLng): Double {
    val results = FloatArray(1)
    Location.distanceBetween( // Calculate the distance between two LatLng points
        from.latitude, from.longitude, // Set the starting point
        to.latitude, to.longitude, // Set the ending point
        results // Set the result
    )
    return results[0] / 1000.0 // Return the distance in kilometers
}
