package com.main.wheres_the_craic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.main.wheres_the_craic.navigation.AppNavigation
import com.main.wheres_the_craic.ui.components.TopBarNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController() // Create a NavController
            val backStackEntry by navController.currentBackStackEntryAsState() // Get the current back stack entry
            val currentRoute = backStackEntry?.destination?.route // Get the route of the current destination

            Scaffold( // Create a Scaffold
                topBar = { // Create a TopBar
                    TopBarNavigation( // Use the TopBarNavigation composable
                        currentRoute = currentRoute, // Pass the current route
                        onSelect = { route -> // Handle selection
                            navController.navigate(route) { // Navigate to the route
                                launchSingleTop = true // Launch single top
                                restoreState = true // Restore state
                                //
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                            }
                        }
                    )
                }
            ) { inner ->
                // Respect top bar padding; then show your NavHost
                Box(Modifier.padding(inner)) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}
