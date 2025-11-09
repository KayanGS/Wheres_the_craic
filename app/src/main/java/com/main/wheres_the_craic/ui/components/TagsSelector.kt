package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
    categories: Map<String, List<String>>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    // Shows 3 chips per row
    val chipsPerRow = 3

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // Spacing between rows
        categories.forEach { (title, tags) -> // Title and tags for each category
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp)) // Spacing between title and tags
            tags.chunked(chipsPerRow).forEach { chunk ->
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
}

val TAGS_BY_CATEGORY = mapOf(
    "Music & Atmosphere" to listOf(
        "Traditional Irish",
        "Live Band",
        "DJ/Electronic",
        "Rock",
        "Pop",
        "Karaoke",
        "Alternative",
        "Queer"
    ),
    "Crowd Age" to listOf("18–24", "25–34", "35–49", "50+"),
    "Perks & Features" to listOf(
        "Drink promos",
        "Free snacks",
        "Outdoor seating",
        "TV Sports",
        "Dog-friendly",
        "Wheelchair accessible"
    ),
    "Vibe" to listOf("Cozy", "Loud", "Dance floor")
)