// Filepath: com/main/wheres_the_craic/data/Pub.kt
package com.main.wheres_the_craic.data

data class Pub(
    val pubId: String,
    val pubName: String,
    val pubAddress: String,
    val pubCity: String,
    val pubRatting: Double, // Will probably be from 0 to 5, to match the google maps api
    val pubDistanceKM: Double?,
    val isPubOpenNow: Boolean,
    // I wanted to create my own crowd system, that would go from 0=cold, 1=warm, 2=hot, 3=very hot
    // And a different thermometer would be show for each pub crowd level
    val pubCrowdLevel: Int,
    val pubDescription: String = ""
)

// Fake Pubs for now, later they will be substituded by real pubs
object FakePubs {
    private val fakePubs = listOf(
        Pub(
            pubId = "1",
            pubName = "Pub 1",
            pubAddress = "Address 1",
            pubCity = "City 1",
            pubRatting = 4.5,
            pubDistanceKM = 0.5,
            isPubOpenNow = true,
            pubCrowdLevel = 2,
            pubDescription = "Very good"
        ),
        Pub(
            pubId = "2",
            pubName = "Pub 2",
            pubAddress = "Address 2",
            pubCity = "City 2",
            pubRatting = 3.5,
            pubDistanceKM = 1.5,
            isPubOpenNow = false,
            pubCrowdLevel = 4,
            pubDescription = "Meh"
        )
    )

    fun getAll(): List<Pub> = fakePubs
    fun getById(id: String?): Pub? = fakePubs.firstOrNull { it.pubId == id}
}