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

@Composable
fun TagsSelector(
    allTags: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    // Simple fixed columns layout: 3 chips per row (keeps it dependency-free)
    val perRow = 3
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        allTags.chunked(perRow).forEach { chunk ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                chunk.forEach { tag ->
                    FilterChip(
                        selected = tag in selected,
                        onClick = { onToggle(tag) },
                        label = { Text(tag) }
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