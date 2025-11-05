package com.main.wheres_the_craic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.main.wheres_the_craic.data.FakePubs

@Composable
fun PubsListScreen(onCheckInClick: (String) -> Unit) {

    val pubs = FakePubs.getAll() // Return all the pubs in the list

    LazyColumn( // Lazy column to display the list of pubs
        modifier = Modifier.fillMaxSize(), // Fill the max size of the screen
        contentPadding = PaddingValues(12.dp), // Padding around the list
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spacing between the items
    ) {
        items(pubs) { pub -> // Iterate through the list of pubs
            Card( // For each pub create a card to display the pub
                modifier = Modifier // Modifier of the card
                    .fillMaxWidth() // Fill the max width of the screen
                    // When clicked, navigate to the check-in screen, passing the pub Id as parameter
                    .clickable { onCheckInClick(pub.pubId) }
            ) {
                Column(Modifier.padding(16.dp)) { // Create a column to display pub information
                    // Display the pub Name
                    Text(pub.pubName, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp)) // Spacing between items
                    Text( //Display pub Address and City
                        text = "${pub.pubAddress}, ${pub.pubCity}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1, // Max number of lines to display
                        overflow = TextOverflow.Ellipsis // If the text overflow, show ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp)) // Spacing between items
                    // Changes what will be displayed based on if the pub is open or not
                    val isOpenText = if (pub.isPubOpenNow) "Open now" else "Closed"
                    // Makes pub distance a string to display
                    val pubDistance = pub.pubDistanceKM.toString() + "km" //

                    Text(
                        text = "${pub.pubRatting} " +
                                "• $pubDistance " +
                                "• $isOpenText " +
                                "• Craic: ${pub.pubCrowdLevel}/4 ",
                        style = MaterialTheme.typography.bodySmall
                    )


                }

            }
        }
    }

}