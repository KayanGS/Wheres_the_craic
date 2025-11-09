package com.main.wheres_the_craic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.main.wheres_the_craic.ui.screens.MapScreen
import com.main.wheres_the_craic.ui.screens.CheckInScreen
import com.main.wheres_the_craic.ui.screens.PubsListScreen

/**
 * Hold all navigation Routes
 */
object Routes {
    const val MAP_SCREEN = "map_screen"
    const val PUBS_LIST_SCREEN = "pubs_list_screen"
    const val CHECK_IN_SCREEN = "check_in_screen"
}

/**
 * The main navigation component for the application.
 * @param navController Controller to manage navigation
 */
@Composable
fun AppNavigation(navController: NavHostController) {

    // Define the navigation graph
    NavHost(navController = navController, startDestination = Routes.MAP_SCREEN) {

        // ############### MAP SCREEN ###############
        composable(Routes.MAP_SCREEN) {
            MapScreen(
                onPubSelected = { pubId: String? -> // Handle pub selection
                    // Navigate to the pubs list screen with the selected pub ID
                    navController.navigate(Routes.PUBS_LIST_SCREEN)
                }
            )
        }
        // ############### MAP SCREEN ###############

        // ############### PUBS LIST SCREEN ###############
        composable(Routes.PUBS_LIST_SCREEN) {
            PubsListScreen(
                onCheckInClick = { pubId: String? -> // Handle check-in click
                    // Navigate to the check-in screen with the selected pub ID
                    navController.navigate("${Routes.CHECK_IN_SCREEN}/$pubId")
                }
            )
        }
        // ############### PUBS LIST SCREEN ###############

        // ############### Check In SCREEN ###############
        composable("${Routes.CHECK_IN_SCREEN}/{pubId}") { backStackEntry ->
            // Get the pub ID from route arguments
            val pubId = backStackEntry.arguments?.getString("pubId")
            CheckInScreen(pubId = pubId, onBack = { navController.popBackStack() })
        }
        // ############### Check In SCREEN ###############
    }

}

