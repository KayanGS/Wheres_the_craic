package com.main.wheres_the_craic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.main.where_s_the_craic.ui.screens.CheckInScreen
import com.main.where_s_the_craic.ui.screens.MapScreen
import com.main.where_s_the_craic.ui.screens.PubDetailsScreen


object Routes {
    const val MAP_SCREEN = "map_screen"
    const val PUB_DETAILS_SCREEN = "pub_details_screen"
    const val CHECK_IN_SCREEN = "check_in_screen"
}

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Routes.MAP_SCREEN) {
        composable(Routes.MAP_SCREEN) {
            MapScreen(
                onPubSelected = { pubId: String ->
                    navController.navigate("${Routes.PUB_DETAILS_SCREEN}/$pubId")
                }
            )
        }

        composable("${Routes.PUB_DETAILS_SCREEN}/{pubId}") { backStackEntry ->
            val pubId = backStackEntry.arguments?.getString("pubId")
            PubDetailsScreen(
                pubId = pubId,
                onCheckInClick = {
                    navController.navigate("${Routes.CHECK_IN_SCREEN}/$pubId")
                }
            )
        }

        composable("${Routes.CHECK_IN_SCREEN}/{pubId}") { backStackEntry ->
            val pubId = backStackEntry.arguments?.getString("pubId")
            CheckInScreen(pubId = pubId)
        }
    }

}

