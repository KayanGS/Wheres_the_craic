// Filepath: com/main/wheres_the_craic/data/CheckInRepository.kt
package com.main.wheres_the_craic.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val PUB_CHECKINS_COLLECTION = "pub_checkins"

private suspend fun saveInDatabasePubCheckIn(
    pubId: String,
    pubFields: Map<String, Any?>
) {
    Firebase.firestore
        .collection(PUB_CHECKINS_COLLECTION) // Save the pub data to the database
        .document(pubId) // Use the pub ID as the document ID
        .set(pubFields, SetOptions.merge()) // Set the data
        .await() //Wait for the result
}

/**
 * Saves the check-in tags for a pub.
 * @param pubId The ID of the pub.
 * @param tags The set of tags to save.
 */
suspend fun savePubCheckInTags(
    pubId: String,
    tags: Set<String>,
) {
    // Get the pub data, its ID, the tags, the current time and increment the crowd
    val pubFields = mutableMapOf<String, Any?>(
        "pubId" to pubId,
        "tags" to tags.toList(),
        "updatedAt" to System.currentTimeMillis(),
    )

    tags.forEach { tag -> // For each tag
        val path = "tagCounts.$tag"   // Create the path for the tag
        pubFields[path] = FieldValue.increment(1) // Increment the tag count
    }
    saveInDatabasePubCheckIn(pubId, pubFields) // Save the pub data to the database
}

/**
 * Increments the crowd count for a pub.
 * @param pubId The ID of the pub.
 */
suspend fun incrementPubCrowd(pubId: String) {
    // Increment the crowd count by 1
    val pubFields = mapOf(
        "pubId" to pubId, // Set the pub ID
        "crowdCount" to FieldValue.increment(1) // Increment the crowd count
    )

    saveInDatabasePubCheckIn(pubId, pubFields) // Save the pub data to the database
}

/**
 * Fetches the check-in data for a pub.
 *@property tags The set of tags.
 *@property crowdCount The crowd count.
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

    val tagsList = snapshot.get("tags") as? List<*> ?: emptyList<Any?>() // Return the pub tags
    val tags = tagsList.filterIsInstance<String>().toSet() // Convert the list to a set of strings
    val crowdCount = snapshot.getLong("crowdCount") // Return the crowd count

    return PubCheckInData( // Return the pub check-in data
        tags = tags, // Set of tags
        crowdCount = crowdCount // The Crowd Count
    )
}