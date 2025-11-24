package com.main.wheres_the_craic.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.main.wheres_the_craic.data.PlaceResult
import com.google.android.gms.location.LocationServices
import com.main.wheres_the_craic.data.fetchNearbyPubs
import com.main.wheres_the_craic.R
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
            println("DEBUG pubs count: ${nearbyPubsResults.size}")
            nearbyPubs = nearbyPubsResults
        } catch (e: Exception) {
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Iterate through nearby pubs
                items(nearbyPubs) { pub ->
                    // Distance between user and pub
                    val distanceKm = distanceInKm(
                        userPosition,
                        LatLng(pub.pubLatitude, pub.pubLongitude)
                    )
                    // Check if pub is open or closed
                    val isOpenText = when (pub.isOpenNow) {
                        true -> "Open now"
                        false -> "Closed"
                        null -> ""
                    }
                    //Pub rating
                    val ratingText = pub.rating?.let { "%.1f".format(it) } ?: "-"
                    // Build photo URL from photo_reference, if available
                    val photoUrl = pub.photoReference?.let { ref ->
                        "https://maps.googleapis.com/maps/api/place/photo" +
                                "?maxwidth=400" +
                                "&photo_reference=$ref" +
                                "&key=${context.getString(R.string.google_maps_key)}"
                    }
                    Card( // Create a card for each pub
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCheckInClick(pub.pubId ?: "") }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // Pub photo
                            if (photoUrl != null) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Pub photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Name from Google Places
                            Text(
                                pub.pubName,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            //Rating - distance - and status
                            Text(
                                text = "⭐ $ratingText • ${"%.1f".format(distanceKm)} km • $isOpenText",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun distanceInKm(from: LatLng, to: LatLng): Double {
    val results = FloatArray(1)
    Location.distanceBetween(
        from.latitude, from.longitude,
        to.latitude, to.longitude,
        results
    )
    return results[0] / 1000.0
}
