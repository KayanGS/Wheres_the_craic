// Filepath: com/main/wheres_the_craic/ui/screens/PubsListScreen.kt
package com.main.wheres_the_craic.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.main.wheres_the_craic.data.PlaceResult
import com.google.android.gms.location.LocationServices
import com.main.wheres_the_craic.data.fetchNearbyPubs
import com.main.wheres_the_craic.R
import com.main.wheres_the_craic.ui.components.PubPreviewCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
@SuppressLint("MissingPermission")
fun PubsListScreen(onCheckInClick: (String) -> Unit) {

    val context = LocalContext.current

    // Variables
    var hasPermission by remember { mutableStateOf(false) }
    var userPosition by remember { mutableStateOf(LatLng(53.3498, -6.2603)) } // Dublin default
    var locationLoaded by remember { mutableStateOf(false) }
    var nearbyPubs by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }

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

    // Fetch nearby pubs once location is ready
    LaunchedEffect(locationLoaded) {
        if (!locationLoaded) return@LaunchedEffect // If location not loaded, do nothing

        try {
            // Create a coroutine to fetch nearby pubs
            val nearbyPubsResults = withContext(Dispatchers.IO) {
                val apiKey = context.getString(R.string.google_maps_key) // Get api key

                // Fetch nearby pubs
                fetchNearbyPubs(
                    userPosition.latitude,
                    userPosition.longitude,
                    apiKey
                )
            }
            println("DEBUG pubs count: ${nearbyPubsResults.size}")
            nearbyPubs = nearbyPubsResults
        } catch (e: Exception) {
            println("Error fetching nearby pubs in list screen: $e")
            nearbyPubs = emptyList()
        }
    }
    // Display UI based on the current state
    when {
        // If permission is not granted, show a message
        !hasPermission -> {
            Text(
                text = "Please grant location permission to see nearby pubs",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        // If location is not loaded, show a message
        !locationLoaded -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Finding your location...")
            }
        }
        else -> {
            // Else, show the list of nearby pubs
            val apiKey = context.getString(R.string.google_maps_key) // Get api key

            LazyColumn( // Create a lazy column for the list of pubs
                modifier = Modifier.fillMaxSize(), // Fill the max size
                contentPadding = PaddingValues(12.dp), // Add padding
                verticalArrangement = Arrangement.spacedBy(12.dp) // Space between items
            ) {
                items(nearbyPubs) { pub -> // Iterate through nearby pubs
                    PubPreviewCard( // Create a card for each pub
                        pub = pub, // Set the pub
                        userPosition = userPosition, // Set the user position
                        googleMapsApiKey = apiKey, // Set the Google Maps API key
                        modifier = Modifier.fillMaxWidth(), // Fill the max width
                        showButton = false, // Don't show the button
                        onCardClick = { // Handle the card click
                            // Navigate to the check-in screen with the selected pub ID
                            onCheckInClick(pub.pubId ?: "")
                        }
                    )
                }
            }
        }
    }
}