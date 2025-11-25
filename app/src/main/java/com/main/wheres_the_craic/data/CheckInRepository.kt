// Filepath: com/main/wheres_the_craic/data/CheckInRepository.kt
package com.main.wheres_the_craic.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Saves the check-in tags for a pub.
 * @param pubId The ID of the pub.
 * @param tags The set of tags to save.
 */
suspend fun savePubCheckInTags(
    pubId: String,
    tags: Set<String>
) {
    val database = Firebase.firestore // Get the database instance

    // Get the pub data, its ID, the tags and the current time
    val pubData = mapOf(
        "pubId" to pubId,
        "tags" to tags.toList(),
        "updatedAt" to System.currentTimeMillis()
    )

    // Save the pub data to the database
    database.collection("pub_checkins")
        .document(pubId) // Use the pub ID as the document ID
        .set(pubData) // Set the data
        .await() // Wait for the result
}
