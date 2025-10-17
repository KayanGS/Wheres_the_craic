package com.main.where_s_the_craic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.main.where_s_the_craic.navigation.AppNavigation
import com.main.where_s_the_craic.ui.theme.WherestheCraicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WherestheCraicTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)


            }
        }
    }
}
