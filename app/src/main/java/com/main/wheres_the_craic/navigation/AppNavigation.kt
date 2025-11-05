package com.main.wheres_the_craic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.main.wheres_the_craic.ui.screens.MapScreen
import com.main.wheres_the_craic.ui.screens.CheckInScreen
import com.main.wheres_the_craic.ui.screens.PubsListScreen

object Routes {
    const val MAP_SCREEN = "map_screen"
    const val PUBS_LIST_SCREEN = "pubs_list_screen"
    const val CHECK_IN_SCREEN = "check_in_screen"
}

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Routes.MAP_SCREEN) {
        composable(Routes.MAP_SCREEN) {
            MapScreen(
                onPubSelected = { pubId: String? ->
                    navController.navigate(Routes.PUBS_LIST_SCREEN)
                }
            )
        }

        composable(Routes.PUBS_LIST_SCREEN) {
            PubsListScreen(
                onCheckInClick = { pubId: String? ->
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

