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

/**
 * Display a block of details of the pub
 *
 * @param pub The data of the pub object to be shown
 */
@Composable
fun PubDetailsBlock(pub: Pub) {

    // Check if it is open or closed and display message
    val isOpenText = if (pub.isPubOpenNow) "Open now" else "Closed"
    // Makes pub distance a string to display
    val pubDistance = pub.pubDistanceKM.toString() + "km" //

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Display pub Name
        Text(pub.pubName, style = MaterialTheme.typography.titleLarge)
        // Display pub Address and city
        Text("${pub.pubAddress}, ${pub.pubCity}", style = MaterialTheme.typography.bodyMedium)
        Text( // Display rating, distance and open status
            "${pub.pubRatting} • $pubDistance • $isOpenText",
            style = MaterialTheme.typography.bodySmall
        )
        if (pub.pubDescription.isNotBlank()) { // Display description if it exists
            Spacer(modifier = Modifier.height(4.dp)) // Spacing between items
            Text(pub.pubDescription, style = MaterialTheme.typography.bodyMedium)
        }

    }
}
