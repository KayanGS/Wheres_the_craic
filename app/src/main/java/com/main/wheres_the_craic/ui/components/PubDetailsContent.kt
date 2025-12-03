// Filepath: com/main/wheres_the_craic/ui/components/CheckInScreen.kt
package com.main.wheres_the_craic.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.main.wheres_the_craic.R
import com.main.wheres_the_craic.data.PubDetails

/**
 * Displays the details of the pub.
 * @param pub The pub details to display.
 * @param context The current context.
 * @param uriHandler The URI handler for opening URLs.
 * @param currentPhotoIndex The current photo index.
 * @param onCurrentPhotoIndexChange The callback function to update the current photo index.
 * @param selectedTags The selected tags.
 * @param checkedIn Whether the user is checked in.
 * @param onSelectedTagsChange The callback function to update the selected tags.
 * @param dialPhoneNumber The callback function to dial the phone number.
 *
 */
@Composable
fun PubDetailsContent(
    pub: PubDetails,
    context: Context,
    uriHandler: UriHandler,
    currentPhotoIndex: Int,
    onCurrentPhotoIndexChange: (Int) -> Unit,
    selectedTags: Set<String>,
    checkedIn: Boolean,
    onSelectedTagsChange: (Set<String>) -> Unit,
    dialPhoneNumber: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val photos = pub.photoReferences // List of photos
        // If photos are available, show a carrousel of them
        if (photos.isNotEmpty()) { // If there are photos
            val photoRef = photos[currentPhotoIndex] // Get the current photo
            val photoUrl = // Build the photo URL
                "https://maps.googleapis.com/maps/api/place/photo" +
                        "?maxwidth=800" +
                        "&photo_reference=$photoRef" +
                        "&key=${context.getString(R.string.google_maps_key)}"

            Box( // Box to hold the photo
                modifier = Modifier // Modifier for the box
                    .fillMaxWidth() // Fill the width
                    .height(200.dp) // Set height to 200dp
            ) {
                // Photo
                AsyncImage( // Async image for the photo
                    model = photoUrl, // Photo URL
                    contentDescription = "Pub photo", // Content description
                    modifier = Modifier // Modifier for the photo
                        .fillMaxSize() // Fill the available space
                )

                // Arrows for navigate through photos
                Row( // Row to hold the arrows
                    modifier = Modifier // Modifier for the row
                        .fillMaxWidth() // Fill the width
                        .padding(8.dp) // Padding inside the row
                        .align(Alignment.Center), // Align to the Center
                    // Space between items
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text( // Text for the back arrow
                        text = "<", // Set it to be the "<" symbol
                        // Set the style
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier // Modifier for the text
                            .clickable { // Make the arrow clickable
                                // If the current photo is not the first
                                if (currentPhotoIndex > 0) {
                                    onCurrentPhotoIndexChange(currentPhotoIndex - 1) // Decrease the index
                                } else { // If it is the first
                                    // Set the index to the last photo
                                    onCurrentPhotoIndexChange(photos.size - 1)
                                }
                            }
                            .padding(8.dp) // Padding inside the text
                    )
                    Text( // Text for the forward arrow
                        text = ">", // Set it to be the ">" symbol
                        // Set the style
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier // Modifier for the text
                            .clickable { // Make the arrow clickable
                                // If the current photo is not the last
                                if (currentPhotoIndex < photos.size - 1) {
                                    onCurrentPhotoIndexChange(currentPhotoIndex + 1) // Increase the index
                                } else { // If it is the last
                                    onCurrentPhotoIndexChange(0) // Set the index to the first photo
                                }
                            }
                            .padding(8.dp) // Padding inside the text
                    )
                }
            }
        } else { // If there are no photos
            ImagePlaceHolder() // Use the ImagePlaceHolder
        }

        // Pub name
        Text(pub.name, style = MaterialTheme.typography.headlineSmall)

        // Pub address
        pub.formattedAddress?.let { // If there is an address
            Text(it, style = MaterialTheme.typography.bodyMedium) // Address text
        }

        // Divider between items
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))

        // Pub rating + Pub price level
        // If there is a rating show it, else show "No rating"
        val ratingText = pub.rating?.let { "⭐ %.1f".format(it) } ?: "No rating"
        // If there is a price level show it, else show "Price unknown"
        val priceText = when (pub.priceLevel) {
            0 -> "Free"
            1 -> "€"
            2 -> "€€"
            3 -> "€€€"
            4 -> "€€€€"
            else -> "Price unknown"
        }

        // Rating text
        Text("$ratingText • $priceText", style = MaterialTheme.typography.bodyMedium)

        // Divider between items
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))

        // Pub opening hours
        if (pub.currentOpeningHours.isNotEmpty()) { // If there are opening hours
            // Opening hours title
            Text("Opening hours:", style = MaterialTheme.typography.titleSmall)
            // For each line in the opening hours
            pub.currentOpeningHours.forEach { line ->
                // Print the line text
                Text(line, style = MaterialTheme.typography.bodySmall)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

        Text("Contact Info:", style = MaterialTheme.typography.titleSmall)
        // Phone
        pub.formattedPhoneNumber?.let { phone -> // If there is a phone number
            Text( // Text for the phone number
                text = "Phone: $phone", // Phone number text
                style = MaterialTheme.typography.bodySmall, // Set to be smaller
                color = MaterialTheme.colorScheme.primary, // Set to be blue
                textDecoration = TextDecoration.Underline, // Set to be underlined
                modifier = Modifier.clickable { // Make the text clickable
                    // Dial the phone number
                    dialPhoneNumber(phone)
                }
            )
        }
        // Website
        pub.website?.let { websiteUrl -> // If there is a website
            Text(
                text = "Website: $websiteUrl", // Website text
                style = MaterialTheme.typography.bodySmall, // Set to be smaller
                color = MaterialTheme.colorScheme.primary, // Set to be blue
                textDecoration = TextDecoration.Underline, // Set to be underlined
                modifier = Modifier.clickable { // Make the text clickable
                    uriHandler.openUri(websiteUrl) // Open the website
                }
            )
        }

        if (selectedTags.isNotEmpty()) { // If there are selected tags
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Text(
                "Current Selected tags:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                selectedTags.joinToString(", "),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (checkedIn) { // If the user is checked in, show the check-in options
            Spacer(modifier = Modifier.height(2.dp)) // Spacing between items
            // Text for the check-in options
            Text("Check-in options", style = MaterialTheme.typography.titleMedium)
            TagsSelector( // Tags selector for extra tags
                categories = TAGS_BY_CATEGORY, // All available tags
                selected = selectedTags, // Selected tags
                onToggle = { tag ->// Callback when a tag is toggled
                    // If the tag is already selected, remove it; otherwise, add it
                    val newSelected =
                        if (tag in selectedTags) {
                            selectedTags - tag
                        } else selectedTags + tag

                    onSelectedTagsChange(newSelected)
                }
            )
        }
    }
}
