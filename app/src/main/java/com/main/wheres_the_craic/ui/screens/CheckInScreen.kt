// Filepath: com/main/wheres_the_craic/ui/screens/CheckInScreen.kt
package com.main.wheres_the_craic.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.main.wheres_the_craic.data.PubDetails
import com.main.wheres_the_craic.R
import com.main.wheres_the_craic.data.fetchPubDetails
import com.main.wheres_the_craic.data.getPubCheckIn
import com.main.wheres_the_craic.data.savePubCheckInTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.main.wheres_the_craic.data.incrementPubCrowd
import com.main.wheres_the_craic.ui.components.PubDetailsContent


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
    var currentPhotoIndex by remember { mutableIntStateOf(0) }
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
                println("Error saving tags for pub $id: $e")
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
            println("Error loading pub details for id=$pubId: $e")
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
                                    incrementPubCrowd(id)
                                } catch (e: Exception) { // If there is an error, show error message
                                    println("Error incrementing crowd for pub $id: $e")
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
                PubDetailsContent(
                    pub = pub,
                    context = context,
                    uriHandler = uriHandler,
                    currentPhotoIndex = currentPhotoIndex,
                    onCurrentPhotoIndexChange = { newIndex ->
                        currentPhotoIndex = newIndex
                    },
                    selectedTags = selectedTags,
                    checkedIn = checkedIn,
                    onSelectedTagsChange = { newSelected ->
                        selectedTags = newSelected
                    },
                    dialPhoneNumber = { phone -> dialPhoneNumber(context, phone) }
                )
            }
        }
    }
}

/**
 * Dial the phone number.
 *
 * @param context The current context.
 * @param phoneNumber The phone number to dial.
 */
private fun dialPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { // Intent to dial the phone number
        data = "tel:$phoneNumber".toUri() // Set the data to the phone number
    }
    context.startActivity(intent)
}