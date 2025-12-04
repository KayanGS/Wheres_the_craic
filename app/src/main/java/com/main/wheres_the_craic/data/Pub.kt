// Filepath: com/main/wheres_the_craic/data/Pub.kt
package com.main.wheres_the_craic.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class PubDetails(
    val placeId: String,
    val name: String,
    val formattedAddress: String?,
    val photoReferences: List<String>,
    val url: String?,
    val currentOpeningHours: List<String>,
    val formattedPhoneNumber: String?,
    val website: String?,
    val priceLevel: Int?,
    val rating: Double?
)

// still in PubDetailsRepository.kt

fun fetchPubDetails(
    placeId: String,
    apiKey: String
): PubDetails? {
    val url = "https://maps.googleapis.com/maps/api/place/details/json" +
            "?place_id=$placeId" +
            "&fields=name,formatted_address,photo,url," +
            "current_opening_hours,formatted_phone_number,website,price_level,rating" +
            "&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    val response = client.newCall(request).execute()

    response.use { resp ->
        val body = resp.body?.string() ?: return null
        val json = JSONObject(body)

        val status = json.optString("status", "UNKNOWN")
        if (status != "OK") return null

        val result = json.optJSONObject("result") ?: return null

        val name = result.optString("name", "Unknown")
        val formattedAddress = result.optString("formatted_address")

        // photos → list of photo_reference
        val photoRefs = mutableListOf<String>()
        val photosArray = result.optJSONArray("photos")
        if (photosArray != null) {
            for (i in 0 until photosArray.length()) {
                val ref = photosArray.optJSONObject(i)
                    ?.optString("photo_reference")
                if (!ref.isNullOrBlank()) {
                    photoRefs.add(ref)
                }
            }
        }

        val urlResult = result.optString("url")

        // current_opening_hours.weekday_text → list of strings
        val openingHours = mutableListOf<String>()
        val openingObj = result.optJSONObject("current_opening_hours")
        val weekdayArray = openingObj?.optJSONArray("weekday_text")
        if (weekdayArray != null) {
            for (i in 0 until weekdayArray.length()) {
                openingHours.add(weekdayArray.optString(i))
            }
        }

        val phone = result.optString("formatted_phone_number")
        val website = result.optString("website")

        val priceLevel = if (result.has("price_level")) {
            result.getInt("price_level")
        } else null

        val rating = if (result.has("rating")) {
            result.getDouble("rating")
        } else null

        return PubDetails(
            placeId = placeId,
            name = name,
            formattedAddress = formattedAddress,
            photoReferences = photoRefs,
            url = urlResult,
            currentOpeningHours = openingHours,
            formattedPhoneNumber = phone,
            website = website,
            priceLevel = priceLevel,
            rating = rating
        )
    }
}
