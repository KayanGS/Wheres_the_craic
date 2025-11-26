// Filepath: com/main/wheres_the_craic/data/CheckInRepository.kt
package com.main.wheres_the_craic.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
        .set(pubData, SetOptions.merge())// Set the data
        //.set(pubData)
        .await() // Wait for the result
}

/**
 * Increments the crowd count for a pub.
 * @param pubId The ID of the pub.
 */
suspend fun incrementPubCrowd(pubId: String) {
    val db = Firebase.firestore // Get the database instance

    // Increment the crowd count by 1
    val data = mapOf(
        "pubId" to pubId,
        "crowdCount" to FieldValue.increment(1)
    )

    // Save the pub data to the database
    db.collection("pub_checkins")
        .document(pubId) // Use the pub ID as the document ID
        .set(data, SetOptions.merge()) // Set the data
        .await() // Wait for the result
}

/**
 * Fetches the check-in data for a pub.
 * @param pubId The ID of the pub.
 */
data class PubCheckInData(
    val tags: Set<String>, // Set of tags
    val crowdCount: Long? // The Crowd Count
)

/**
 * Fetches the check-in data for a pub.
 * @param pubId The ID of the pub.
 */
suspend fun getPubCheckIn(pubId: String): PubCheckInData? {
    val db = Firebase.firestore // Get the database instance

    // Get the pub data from the database
    val snapshot = db.collection("pub_checkins")
        .document(pubId) // Use the pub ID as the document ID
        .get() // Get the data
        .await() // Wait for the result

    if (!snapshot.exists()) return null // If the snapshot does not exist, return null

    val tagsList = snapshot.get("tags") as? List<*> ?: emptyList<Any>() // Return the pub tags
    val tags = tagsList.filterIsInstance<String>().toSet() // Convert the list to a set of strings
    val crowdCount = snapshot.getLong("crowdCount") // Return the crowd count

    return PubCheckInData( // Return the pub check-in data
        tags = tags, // Set of tags
        crowdCount = crowdCount // The Crowd Count
    )
}