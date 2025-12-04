//Filepath com/main/wheres_the_craic/ui/components/PubPreviewCard.kt
package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.main.wheres_the_craic.data.PlaceResult
import com.main.wheres_the_craic.util.distanceInKm
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star


/**
 * Small reusable card showing a pub photo, name, distance, rating and open/closed.
 * @param pub The pub to display.
 * @param userPosition The user's position.
 * @param googleMapsApiKey The Google Maps API key.
 * @param modifier The modifier to apply to the card.
 * @param showCloseButton Whether to show a close button.
 * @param onCloseClick The action to perform when the close button is clicked.
 * @param showButton Whether to show a button.
 * @param buttonText The text to display on the button.
 * @param onButtonClick The action to perform when the button is clicked.
 * @param onCardClick The action to perform when the card is clicked.
 */
@Composable
fun PubPreviewCard(
    pub: PlaceResult,
    userPosition: LatLng,
    googleMapsApiKey: String,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = false,
    onCloseClick: (() -> Unit)? = null,
    showButton: Boolean = false,
    buttonText: String = "Open details & Check-in",
    onButtonClick: (() -> Unit)? = null,
    onCardClick: (() -> Unit)? = null,
    showNavigateButton: Boolean = false,
    onNavigateClick: (() -> Unit)? = null
) {
    Card( // Card with pub information
        modifier = modifier // Apply modifier
            .then( // Then apply the following
                if (onCardClick != null) { // If onCardClick is not null, apply clickable
                    Modifier.clickable { onCardClick() } // Apply clickable
                } else { // Else, apply no clickable
                    Modifier // Apply no clickable
                }
            ),
        shape = RoundedCornerShape(16.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Create elevation
        colors = CardDefaults.cardColors( // Create colors
            containerColor = MaterialTheme.colorScheme.surface // The container color
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // Column to hold the card content
        ) {
            // Row with pub name and close button
            Row(
                verticalAlignment = Alignment.CenterVertically, // Align items vertically
                modifier = Modifier.fillMaxWidth() // Fill the max width
            ) {
                Text(
                    text = pub.pubName, // Pub Name
                    style = MaterialTheme.typography.titleMedium.copy( // Makes text medium
                        fontWeight = FontWeight.SemiBold // Makes text bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    maxLines = 2
                )

                // If close button is enabled and clicked is not null
                if (showCloseButton && onCloseClick != null) {
                    Text(
                        text = "✕", // Display "X" to be the close button for the card
                        style = MaterialTheme.typography.titleMedium, // Makes text title medium
                        modifier = Modifier // Apply Modifier
                            .clickable { onCloseClick() } // Apply clickable
                            .padding(4.dp) // Apply padding
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image (if we have a photoReference)
            val photoRef = pub.photoReference
            if (photoRef != null) {
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo" +
                            "?maxwidth=400" +
                            "&photo_reference=$photoRef" +
                            "&key=$googleMapsApiKey"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Pub photo",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Distance
            val distanceKm by remember(userPosition, pub.pubLatitude, pub.pubLongitude) {
                mutableDoubleStateOf(
                    distanceInKm(
                        userPosition,
                        LatLng(pub.pubLatitude, pub.pubLongitude)
                    )
                )
            }

            // Rating + open/closed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Distance
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Distance",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "%.2f km away".format(distanceKm),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Rating
                val ratingText = pub.rating?.let { "%.1f".format(it) } ?: "-"
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = ratingText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Status chip (open/closed)
                val openText = when (pub.isOpenNow) {
                    true -> "Open now"
                    false -> "Closed"
                    null -> "Hours unknown"
                }

                val statusColor = when (pub.isOpenNow) {
                    // Different colours for open/closed
                    true -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    false -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    null -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
                }

                val statusTextColor = when (pub.isOpenNow) {
                    // Different colours for open/closed text
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.error
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Align vertically
                    modifier = Modifier
                        .background(
                            color = statusColor, // Background colour based o pub status
                            shape = RoundedCornerShape(50) // Rounded corners
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp) //Apply padding
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime, // Apply clock icon
                        contentDescription = "Status", // Apply content description
                        tint = statusTextColor // Apply text colour based on pub status
                    )
                    Text(
                        text = openText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusTextColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Bottom actions
            if (showButton || showNavigateButton) {
                Spacer(modifier = Modifier.height(12.dp)) // Apply a spacer

                Row( // Row to hold the buttons
                    modifier = Modifier.fillMaxWidth(), // Fill the width
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Apply spacing
                ) {
                    // If navigate button is enabled and clicked is not null
                    if (showNavigateButton && onNavigateClick != null) {
                        TextButton( // Text button for navigate
                            onClick = { onNavigateClick() }, // Handle click
                            modifier = Modifier.weight(1f) // Apply weight
                        ) {
                            Icon( // Icon for the button
                                imageVector = Icons.Filled.Directions, // Apply directions icon
                                contentDescription = "Directions", // Apply content description
                                modifier = Modifier.padding(end = 4.dp) // Apply padding
                            )
                            Text("Directions") // Apply text
                        }
                    }
                    // If button is enabled and clicked is not null
                    if (showButton && onButtonClick != null) {
                        Button( // Button for the action
                            onClick = { onButtonClick() }, // Handle click
                            modifier = Modifier.weight(1f) // Apply weight
                        ) {
                            Text(buttonText) // Apply text
                        }
                    }
                }
            }
        }
    }
}
