package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays the tags.
 *
 * @param allTags The list of all possible tags.
 * @param selected The current selected tags.
 * @param onToggle Callback function when clicked will change the state of a tag
 */
@Composable
fun TagsSelector(
    allTags: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    // Shows 3 chips per row
    val chipsPerRow = 3

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Spacing between rows
        allTags.chunked(chipsPerRow).forEach { chunk -> // Split the tags into rows
            Row( // Row for each chunk
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between chips
                modifier = Modifier.fillMaxWidth() // Fill the width
            ) {
                chunk.forEach { tag -> // For each tag in the chunk
                    FilterChip( // Chip for each tag
                        selected = tag in selected, // Check if the tag is selected
                        onClick = { onToggle(tag) }, // Callback when clicked
                        label = { Text(tag) } // Display the tag
                    )
                }
            }
        }
    }
}

val TAGS = listOf(
    // Music/atmosphere
    "Trad", "Live Band", "DJ/Electronic", "Rock", "Pop", "Karaoke",
    // Crowd / age vibe
    "18–24", "25–34", "35–49", "50+",
    // Perks / features
    "Drink promos", "Free snacks", "Outdoor seating", "TV Sports",
    "Cozy", "Loud", "Dance floor", "Dog-friendly", "Wheelchair accessible"
)