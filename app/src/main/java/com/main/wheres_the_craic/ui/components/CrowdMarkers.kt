//Filepath: com/main/wheres_the_craic/ui/components
package com.main.wheres_the_craic.ui.components

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.main.wheres_the_craic.R
import androidx.core.graphics.scale

/**
 * Represents a set of custom marker icons for different crowd levels.
 *
 * @property frozen The icon for level 1.
 * @property cold The icon for level 2.
 * @property warm The icon for level 3.
 * @property hot The icon for level 4.
 * @property onFire The icon for level 5.
 */
data class CrowdMarkerIcons(
    val frozen: BitmapDescriptor,
    val cold: BitmapDescriptor,
    val warm: BitmapDescriptor,
    val hot: BitmapDescriptor,
    val onFire: BitmapDescriptor
)

/**
 * Loads and caches custom marker icons as BitmapDescriptor.
 * It fixed the very delayed time to open a marker.
 */
@Composable
fun rememberCrowdMarkerIcons(): CrowdMarkerIcons {
    val context = LocalContext.current // Get the current context

    return remember {
        // Remember the icons
        @SuppressLint("LocalContextResourcesRead") // Ignore the warning
        fun loadScaled(resId: Int): BitmapDescriptor { // Load the scaled icon
            val bmp = BitmapFactory.decodeResource(context.resources, resId) // Get the bitmap
            val scaled = bmp.scale(200, 200) // Scale the bitmap
            return BitmapDescriptorFactory.fromBitmap(scaled) // Return the scaled bitmap
        }
        CrowdMarkerIcons( // Return the icons
            frozen = loadScaled(R.drawable.frozenmarker_lvl1), // Load frozen icon
            cold = loadScaled(R.drawable.coldmarker_lvl2), // Load cold icon
            warm = loadScaled(R.drawable.warmmarker_lvl3), // Load warm icon
            hot = loadScaled(R.drawable.hotmarker_lvl4), // Load hot icon
            onFire = loadScaled(R.drawable.onfiremarker_lvl5) // Load on fire icon
        )
    }
}

/**
 * Converts the crowd count to a customized marker.
 *
 * @param crowdCount The crowd count to convert.
 */
fun crowdCountToMarker(crowdCount: Long, icons: CrowdMarkerIcons): BitmapDescriptor {
    return when {
        crowdCount <= 10L -> icons.frozen // If crowd is less or equal to 10, return frozen icon
        crowdCount <= 20L -> icons.cold // If crowd is less or equal to 20, return cold icon
        crowdCount <= 30L -> icons.warm // If crowd is less or equal to 30, return warm icon
        crowdCount <= 40L -> icons.hot // If crowd is less or equal to 40, return hot icon
        else -> icons.onFire // If crowd is greater than 40, return on fire icon

    }
}