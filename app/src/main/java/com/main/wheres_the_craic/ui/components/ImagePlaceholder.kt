package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Display a placeholder image
 */
@Composable
fun ImagePlaceHolder() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant, // Use a variant color
        modifier = Modifier // Apply modifiers
            .fillMaxWidth() // Fill the width of the parent
            .aspectRatio(16f / 9f) // Maintain aspect ratio
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Image Placeholder") // Display text
        }
    }
}