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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.main.wheres_the_craic.R


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
    var pubs by remember { mutableStateOf<List<Place>>(emptyList()) }

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
        // Initialize the Places client
        val placesClient: PlacesClient = Places.createClient(context)

        val placesFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.TYPES,
            Place.Field.ADDRESS
        )

        // Create a FindCurrentPlaceRequest to fetch the current place
        val request = FindCurrentPlaceRequest.newInstance(placesFields)

        placesClient.findCurrentPlace(request).addOnSuccessListener { response ->
            //TO-DO
        }


        // Fetch last location
        val fused = LocationServices.getFusedLocationProviderClient(context) // get location client
        fused.lastLocation // get last location
            .addOnSuccessListener { loc -> // if success
                if (loc != null) { // if location is not null
                    userPosition = LatLng(loc.latitude, loc.longitude) // update user position
                }
                locationLoaded = true // update location loaded

            }
            .addOnFailureListener {
                // fall back to default; still let UI proceed
                locationLoaded = true
            }
    }

    // Create the camera position state, with a default position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPosition, 14f)
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
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = true
                )
            ) {
            }
        }
    }
}