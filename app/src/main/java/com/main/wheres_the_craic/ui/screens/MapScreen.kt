package com.main.wheres_the_craic.ui.screens

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState


/**
 * This Screen will be the main screen of the app, will show the map using google maps, and locate
 * the pubs closer to the user and show in the map, clicking in a pub will redirect the user to the
 * check-in screen
 */
@Composable
fun MapScreen(onPubSelected: (String) -> Unit = {}) {

//    // Temporary for testing
//    var message by remember { mutableStateOf("Waiting for permission...") }
//    // Call permission composable
//    RequestLocationPermission {
//        message = "Permission granted!"
//    }
//    Surface {
//        Text(text = message)
//    }
    val context = LocalContext.current // Get the current context
    // Variable to hold user position
    var userPosition by remember { mutableStateOf(LatLng(53.3498, -6.2603)) }
    var locationLoaded by remember { mutableStateOf(false) } // Check if location was loaded

    // Call location permission composable
    RequestLocationPermission {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Call fused location provider
            val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
            // Get last location and set it to userPosition
            fusedLocation.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userPosition = LatLng(location.latitude, location.longitude)
                }
                locationLoaded = true // Set locationLoaded to true to
            }.addOnFailureListener {
                locationLoaded = false
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userPosition, 14f)

            }

            if (!locationLoaded) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
            }
        }


    }
}