// Filepath: com/main/wheres_the_craic/ui/screens/MapScreen.kt
package com.main.wheres_the_craic.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.main.wheres_the_craic.R
import com.main.wheres_the_craic.data.PlaceResult
import com.main.wheres_the_craic.data.fetchNearbyPubs
import com.main.wheres_the_craic.data.getPubCheckIn
import com.main.wheres_the_craic.ui.components.PubPreviewCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This Screen will be the main screen of the app, will show the map using google maps, and locate
 * the pubs closer to the user and show in the map, clicking in a pub will redirect the user to the
 * check-in screen
 */
@Composable
@SuppressLint("MissingPermission")
fun MapScreen(onPubSelected: (String) -> Unit = {}) {
    val context = LocalContext.current

    // Variables
    var hasPermission by remember { mutableStateOf(false) }
    var userPosition by remember { mutableStateOf(LatLng(53.3498, -6.2603)) } // Dublin default
    var locationLoaded by remember { mutableStateOf(false) }
    var nearbyPubs by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var selectedPubMarker by remember { mutableStateOf<PlaceResult?>(null) }

    // Ask for permission and handle if permission is granted or denied
    LocationPermission(
        onPermissionGranted = { hasPermission = true },
        onPermissionDenied = { hasPermission = false }
    )
    // After permission, fetch last location, temporarily Dublin
    LaunchedEffect(hasPermission) {
        // If permission is not granted it should do nothing
        if (!hasPermission) return@LaunchedEffect

        if (!Places.isInitialized()) {
            Places.initialize(
                context.applicationContext,
                context.getString(R.string.google_maps_key)
            )
        }

        // Fetch last location
        val fused = LocationServices.getFusedLocationProviderClient(context) // Get location client
        fused.lastLocation // Get last location
            .addOnSuccessListener { loc -> // If success
                if (loc != null) { // If location is not null
                    userPosition = LatLng(loc.latitude, loc.longitude) // Update user position
                }
                locationLoaded = true // Update location loaded

            }
            .addOnFailureListener {
                // Fall back to default; still let UI proceed
                locationLoaded = true
            }
    }

    LaunchedEffect(locationLoaded) {
        if (!locationLoaded) return@LaunchedEffect // If location not loaded, do nothing

        val searchRadius = 5000 // 5km

        try {
            // Create a coroutine to fetch nearby pubs
            val nearbyPubsResults = withContext(Dispatchers.IO) {
                val apiKey = context.getString(R.string.google_maps_key) // Get api key

                // Fetch nearby pubs
                fetchNearbyPubs(
                    userPosition.latitude,
                    userPosition.longitude,
                    apiKey,
                    searchRadius
                )
            }

            val pubsCrowd = withContext(Dispatchers.IO) {
                val list = mutableListOf<PlaceResult>()
                for (place in nearbyPubsResults) {
                    val crowd = try {
                        place.pubId?.let { id ->
                            getPubCheckIn(id)?.crowdCount ?: 0L
                        } ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                    list.add(place.copy(crowdCount = crowd))
                }
                list
            }

            nearbyPubs = pubsCrowd
        } catch (e: Exception) {
            nearbyPubs = emptyList()
        }
    }
    // Create the camera position state, with a default position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPosition, 14f)
    }
    // Update the camera position when the user position changes
    LaunchedEffect(userPosition) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(userPosition, 14f)
    }

    // Will render UI based on the current state
    when {
        !hasPermission -> { // If permission is not granted, show a message
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Please grant location permission")
            }
        }

        !locationLoaded -> { // If location is not loaded, show a loading indicator
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> { // Else, shows the map
            Box(Modifier.fillMaxSize()) {
                GoogleMap( // Create the map
                    modifier = Modifier.fillMaxSize(), // Fill the max size
                    cameraPositionState = cameraPositionState, // Set the camera position state
                    // Enable the user's location
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings( // Set the UI settings
                        myLocationButtonEnabled = true, // Enable the user's location button
                        zoomControlsEnabled = true // Enable the zoom controls
                    )
                ) {
                    nearbyPubs.forEach { place -> // For each pub
                        Marker( // Create a marker
                            state = MarkerState( // Set the marker state
                                position = LatLng( // Set the position
                                    place.pubLatitude, // To the pub's latitude
                                    place.pubLongitude // And longitude
                                )
                            ),
                            title = "${place.pubName} (${place.crowdCount})", // Set the title
                            // Set the icon based on the crowd count
                            icon = crowdCountToMarker(context, place.crowdCount),
                            onClick = { // When clicked
                                // Set the selected pub marker to the clicked pub
                                selectedPubMarker = place
                                true
                            }
                        )
                    }
                }
                // Small bottom card when a marker is selected
                selectedPubMarker?.let { pub -> // If a marker is selected
                    PubPreviewCard( // Create a card for the selected pub
                        pub = pub, // Set the pub
                        userPosition = userPosition, // Set the user position
                        // Set the Google Maps API key
                        googleMapsApiKey = context.getString(R.string.google_maps_key),
                        modifier = Modifier // Set the modifier
                            .align(Alignment.BottomCenter) // Align to the bottom center
                            .padding(16.dp) // Add padding
                            .fillMaxWidth(), // Fill the max width
                        showCloseButton = true, // Show the close button
                        // Handle the close button click
                        onCloseClick = { selectedPubMarker = null },
                        showButton = true, // Show the button
                        buttonText = "Open details & Check-in", // Set the button text
                        onButtonClick = { // Handle the button click
                            // Navigate to the check-in screen with the selected pub ID
                            pub.pubId?.let { onPubSelected(it) }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Converts the crowd count to a customized marker.
 *
 * @param crowdCount The crowd count to convert.
 */
fun crowdCountToMarker(context: Context, crowdCount: Long): BitmapDescriptor {

    val markerResId = when { // Set the marker based on the crowd count
        crowdCount <= 10L -> R.drawable.frozenmarker_lvl1      // 0–10 frozen
        crowdCount <= 20L -> R.drawable.coldmarker_lvl2        // 11–20 cold
        crowdCount <= 30L -> R.drawable.warmmarker_lvl3        // 21–30 warm
        crowdCount <= 40L -> R.drawable.hotmarker_lvl4         // 31–40 hot
        else -> R.drawable.onfiremarker_lvl5                   // 40+ on fire
    }
    // Convert the marker to a bitmap
    val convertedToBitmapMarker = BitmapFactory.decodeResource(context.resources, markerResId)

    val targetWidth = 200 // Set the target width
    val targetHeight = 200 // Set the target height

    // Scale the marker to the target size
    val scaled = Bitmap.createScaledBitmap(convertedToBitmapMarker, targetWidth, targetHeight, true)

    return BitmapDescriptorFactory.fromBitmap(scaled) // Return the marker
}