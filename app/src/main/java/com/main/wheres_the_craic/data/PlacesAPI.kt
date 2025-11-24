package com.main.wheres_the_craic.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Represents a place result.
 *
 * @property pubId The ID of the place.
 * @property pubName The name of the place.
 * @property pubLatitude The latitude of the place.
 * @property pubLongitude The longitude of the place.
 */
data class PlaceResult(
    val pubId: String?,
    val pubName: String,
    val pubLatitude: Double,
    val pubLongitude: Double
)

suspend fun fetchNearbyPubs(
    PubLatitude: Double,
    PubLongitude: Double,
    apiKey: String,
    radiusMeters: Int
): List<PlaceResult> {
    // Create the url for the request with the parameters
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$PubLatitude,$PubLongitude" +
            "&radius=$radiusMeters" +
            "&type=bar|bar_and_grill|pub|irish_pub" +
            "&key=$apiKey"

    val client = OkHttpClient() // Create the client instance
    val request = Request.Builder().url(url).build() // Create the request

    val response = client.newCall(request).execute() // Execute the request
    // Parse the response and return the list of pubs
    response.use { resp ->
        // Read the body of the response and return null if the list is empty
        val body = resp.body?.string() ?: return emptyList()
        val json = JSONObject(body) // Parse the response as JSON
        // Read the results json array and return null if the list is empty
        val results = json.optJSONArray("results") ?: return emptyList()

        val pubs = mutableListOf<PlaceResult>() // Create a list of pubs
        // Iterate between the results and add each pub to the list
        for (i in 0 until results.length()) {
            val pubObject = results.getJSONObject(i) // Get the current result object
            // Get the name or set it to "Unknown" if it is null
            val pubName = pubObject.optString("name", "Unknown")
            // Get the id or set it to null if it is null
            val pubId = pubObject.optString("place_id", null)
            // Get the Location
            val pubLocation = pubObject.getJSONObject("geometry").getJSONObject("location")
            val pubLatitude = pubLocation.getDouble("lat") // Get the latitude
            val pubLongitude = pubLocation.getDouble("lng") // Get the longitude
            // Add the pub to the list
            pubs.add(PlaceResult(pubId, pubName, pubLatitude, pubLongitude))
        }
        return pubs // Return the list of pubs
    }
}
