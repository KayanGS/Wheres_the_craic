package com.main.wheres_the_craic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.main.wheres_the_craic.data.Pub

@Composable
fun PubDetailsBlock(pub: Pub) {

    val isOpenText = if (pub.isPubOpenNow) "Open now" else "Closed"
    // Makes pub distance a string to display
    val pubDistance = pub.pubDistanceKM.toString() + "km" //

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(pub.pubName, style = MaterialTheme.typography.titleLarge)
        Text("${pub.pubAddress}, ${pub.pubCity}", style = MaterialTheme.typography.bodyMedium)
        Text(
            "${pub.pubRatting} • $pubDistance • $isOpenText",
            style = MaterialTheme.typography.bodySmall
        )
        if (pub.pubDescription.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp)) // Spacing between items
            Text(pub.pubDescription, style = MaterialTheme.typography.bodyMedium)
        }

    }
}
