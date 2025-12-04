//Filepath: com/main/wheres_the_craic/util/distanceInKm.kt
package com.main.wheres_the_craic.util

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.core.net.toUri

/**
 * Calculates the distance in km between two LatLng points.
 * @param from The starting point.
 * @param to The ending point.
 * @return The distance in kilometers.
 */
fun distanceInKm(
    from: com.google.android.gms.maps.model.LatLng,
    to: com.google.android.gms.maps.model.LatLng
): Double {
    val results = FloatArray(1)
    Location.distanceBetween( // Calculate the distance between two LatLng points
        from.latitude, from.longitude, // Set the starting point
        to.latitude, to.longitude, // Set the ending point
        results // Set the result
    )
    return results[0] / 1000.0 // Return the distance in kilometers
}

/**
 * Opens Google Maps with a route to the given coordinates.
 *
 * @param context The current context.
 * @param latitude Destination latitude.
 * @param longitude Destination longitude.
 * @param label Optional label for the place.
 */
fun openGoogleMapsDirections(
    context: Context,
    latitude: Double,
    longitude: Double,
    label: String? = null
) {
    val encodedLabel = label?.let { Uri.encode(it) } // Encode the label if it's not null
    val destination = if (encodedLabel != null) { // If the label is not null
        "$latitude,$longitude($encodedLabel)" // Add the label to the destination
    } else { // Else, just add the coordinates
        "$latitude,$longitude" // Add the coordinates to the destination
    }
    // Create a URI for the Google Maps directions
    val uri = "https://www.google.com/maps/dir/?api=1&destination=$destination".toUri()
    // Create an intent to view the URI
    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps") // Set the package to Google Maps
    }

    context.startActivity(mapIntent)
}