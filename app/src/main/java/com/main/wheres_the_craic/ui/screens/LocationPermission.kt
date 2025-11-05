// Filepath: com/main/wheres_the_craic/ui/screens/LocationPermission.kt
package com.main.wheres_the_craic.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun LocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {

    val context = LocalContext.current

    // Helper function to check location permission
    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED // Check for fine location permission
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED // Check for coarse location permission
        return fine || coarse // Return true if either permission is granted
    }
    // Creates a launcher
    val launcher =
        rememberLauncherForActivityResult( // Launches a permission request
            contract = ActivityResultContracts.RequestPermission() // Request permission
        ) { isGranted -> // Callback with the request result
            if (isGranted || hasLocationPermission()) { // If permission is granted
                onPermissionGranted() // Call the callback function with onPermissionGranted
            } else { // If permission is denied
                onPermissionDenied() // Call the callback function with onPermissionDenied
            }
        }

    LaunchedEffect(Unit) {
        if (hasLocationPermission()) {
            onPermissionGranted()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}