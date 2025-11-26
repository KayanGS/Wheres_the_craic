// Filepath: com/main/wheres_the_craic/ui/screens/CheckInScreen.kt
package com.main.wheres_the_craic.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.main.wheres_the_craic.data.PubDetails
import com.main.wheres_the_craic.R
import com.main.wheres_the_craic.data.fetchPubDetails
import com.main.wheres_the_craic.data.getPubCheckIn
import com.main.wheres_the_craic.data.incrementPubCrowd
import com.main.wheres_the_craic.data.savePubCheckInTags
import com.main.wheres_the_craic.ui.components.ImagePlaceHolder
import com.main.wheres_the_craic.ui.components.TagsSelector
import com.main.wheres_the_craic.ui.components.TAGS_BY_CATEGORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri


/**
 * A screen that displays the details of the pubs and also allows the user to check-in in the pub
 * After checking in the user can select the current pub "vibes" and extra tags, for sharing with
 *  other users
 *
 * @param pubId The unique identifier for the pub
 * @param onBack Callback function that will bring user to the previous screen
 */
@Composable
fun CheckInScreen(pubId: String?, onBack: () -> Unit) {

    val context = LocalContext.current // Get the current context
    val uriHandler = LocalUriHandler.current // Get the URI handler for opening URLs
    // State for the pub details
    var pubDetails by remember { mutableStateOf<PubDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) } // State for loading
    // State for error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var checkedIn by remember { mutableStateOf(false) } // State for the user check-in status
    // State for the current photo index
    var currentPhotoIndex by remember { mutableStateOf(0) }
    // State for the selected tags
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }
    val scope = rememberCoroutineScope() // Remember the coroutine scope
    // Save the tags when the user checks in
    LaunchedEffect(selectedTags, checkedIn) { // Launch the effect
        val id = pubId // Get the pub ID
        if (checkedIn && id != null) { // If the user is checked in and the pub ID is not null
            try { // Try to save the tags
                savePubCheckInTags(id, selectedTags) // Save the tags
            } catch (e: Exception) {
                // If there is an error, show the error message
                errorMessage = "Failed to save tags"
            }
        }
    }
    // Load details when pubId changes
    LaunchedEffect(pubId) {
        if (pubId == null) { // If pubId is null, show error message
            errorMessage = "Invalid pub" // Error Message
            isLoading = false // Stop loading
            return@LaunchedEffect // Return early
        }
        try { // Try to load the details

            val apiKey = context.getString(R.string.google_maps_key) // Get the API key
            val details = withContext(Dispatchers.IO) { // Fetch the details in a coroutine
                fetchPubDetails(pubId, apiKey) // Fetch the details
            }

            if (details == null) { // If details are null, show error message
                errorMessage = "Pub not found" // Error Message

            } else { // Else, set the details

                pubDetails = details // Set the details

                // Fetch the check-in data in a coroutine
                val checkInData = withContext(Dispatchers.IO) {

                    getPubCheckIn(pubId) // Fetch the check-in data
                }

                if (checkInData != null) { // If check-in data is not null
                    selectedTags = checkInData.tags // Set the selected tags
                }
            }
        } catch (e: Exception) { // If there is an error, show error message
            errorMessage = "Failed to load pub details" // Error Message

        } finally { // Finally, stop loading
            isLoading = false // Stop Loading
        }
    }

    if (isLoading) { // If loading, show a loading screen
        Surface(Modifier.fillMaxSize()) { // Surface to fill the screen
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // Center the content
                Text("Loading pub details...") // Loading text
            }
        }
        return // Return
    }

    if (errorMessage != null || pubDetails == null) { // If there is an error, show it
        Surface(Modifier.fillMaxSize()) { // Surface to fill the screen
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // Center the content
                Text(errorMessage ?: "Pub not found")  // Error message text
            }
        }
        return // Return
    }
    // from here on, pubDetails is not null
    val pub = pubDetails!! // Set the pub details that cannot be null

    Scaffold( // Main scaffold for the screen
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        bottomBar = { // Full width Check-in button
            if (!checkedIn) { // If the user is not checked in
                // Button
                Button( // Button to check-in
                    onClick = { // When clicked get pub id and increment crowd
                        checkedIn = true // Set checked in to true
                        val id = pubId // Get the pub ID
                        if (id != null) { // If the pub ID is not null
                            scope.launch { // Launch a coroutine
                                try { // Try to increment the crowd
                                    incrementPubCrowd(id) // Increment the crowd
                                } catch (e: Exception) { // If there is an error, show error message
                                    errorMessage = "Failed to increment crowd" // Error Message
                                }
                            }
                        }
                    }, // Check in when clicked
                    modifier = Modifier
                        .fillMaxWidth() // Fill the width
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Padding
                ) {
                    Text("Check-in") // Button Text
                }
            }
        }
    ) { inner -> // Content of the screen
        Column( // Column to organize the content
            modifier = Modifier
                .padding(inner) // Padding inside the screen
                .fillMaxSize() // Fill the available space
                .verticalScroll(rememberScrollState()) // Allow scrolling
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) { // Row for the back button
                IconButton(onClick = onBack) { // Back button
                    Icon( // Icon for the back button
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Icon image
                        contentDescription = "Back" // Button description
                    )
                }
                // Text for the back button
                Text("Back", style = MaterialTheme.typography.bodyMedium)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
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
                                                currentPhotoIndex-- // Decrease the index
                                            } else { // If it is the first
                                                // Set the index to the last photo
                                                currentPhotoIndex = photos.size - 1
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
                                                currentPhotoIndex++ // Increase the index
                                            } else { // If it is the last
                                                currentPhotoIndex =
                                                    0 // Set the index to the first photo
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
                                dialPhoneNumber(context, phone)
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
                                selectedTags =
                                    if (tag in selectedTags) {
                                        selectedTags - tag
                                    } else selectedTags + tag

                            }
                        )
                    }
                }
            }
        }
    }
}

// Simple helper to open the dialer
private fun dialPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    context.startActivity(intent)
}
