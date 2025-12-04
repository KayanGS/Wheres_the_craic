package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.main.wheres_the_craic.data.PlaceResult
import com.main.wheres_the_craic.util.distanceInKm

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
    onCardClick: (() -> Unit)? = null
) {
    Card( // Card with pub information
        modifier = modifier
            .then(
                if (onCardClick != null) {
                    Modifier.clickable { onCardClick() }
                } else {
                    Modifier
                }
            )
    ) {
        Column(Modifier.padding(12.dp)) { // Column to hold the card content

            // Row with pub name and close button
            Row(
                verticalAlignment = Alignment.CenterVertically, // Align items vertically
                modifier = Modifier.fillMaxWidth() // Fill the max width
            ) {
                Text(
                    text = pub.pubName, // Pub Name
                    style = MaterialTheme.typography.titleMedium, // Makes text medium
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                // If close button is enabled and clicked is not null
                if (showCloseButton && onCloseClick != null) {
                    Text(
                        text = "X", // Display "X" to be the close button for the card
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .clickable { onCloseClick() }
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Image (if we have a photoReference)
            val photoRef = pub.photoReference
            if (photoRef != null) {
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo" +
                            "?maxwidth=300" +
                            "&photo_reference=$photoRef" +
                            "&key=$googleMapsApiKey"

                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Pub photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))
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

            Text(
                text = "Distance: %.2f km".format(distanceKm),
                style = MaterialTheme.typography.bodySmall
            )

            // Rating + open/closed
            val ratingText = pub.rating?.let { "⭐ %.1f".format(it) } ?: "No rating"
            val openText = when (pub.isOpenNow) {
                true -> "Open now"
                false -> "Closed"
                null -> "Hours unknown"
            }
            Text(
                text = "$ratingText • $openText",
                style = MaterialTheme.typography.bodySmall
            )

            // Optional button (used in MapScreen)
            if (showButton && onButtonClick != null) {
                Button(
                    onClick = { onButtonClick() },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}
