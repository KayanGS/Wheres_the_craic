package com.main.wheres_the_craic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.main.wheres_the_craic.data.FakePubs
import com.main.wheres_the_craic.ui.components.ImagePlaceHolder
import com.main.wheres_the_craic.ui.components.PubDetailsBlock
import com.main.wheres_the_craic.ui.components.TagsSelector
import com.main.wheres_the_craic.ui.components.TAGS

@Composable
fun CheckInScreen(pubId: String?) {
    val pub = FakePubs.getById(pubId)
    var checkedIn by remember { mutableStateOf(false) }


    if (pub == null) {
        Surface(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pub not found")
            }
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            if (!checkedIn) {
                FloatingActionButton(
                    onClick = { checkedIn = true }
                ) {
                    Text("Check-in")
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePlaceHolder()
            PubDetailsBlock(pub)

            var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }

            if (checkedIn) {
                Spacer(modifier = Modifier.height(4.dp)) // Spacing between items
                Text("Select the vibes", style = MaterialTheme.typography.titleMedium)

                TagsSelector(
                    allTags = TAGS,
                    selected = selectedTags,
                    onToggle = { tag ->
                        selectedTags = if (tag in selectedTags) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                    }
                )
            }
        }
    }
}


