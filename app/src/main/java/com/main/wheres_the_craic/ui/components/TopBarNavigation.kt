// Filepath: com/main/wheres_the_craic/ui/components/TopBarNavigation
package com.main.wheres_the_craic.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.main.wheres_the_craic.navigation.Routes

@Composable
//
fun TopBarNavigation(currentRoute: String?, onSelect: (String) -> Unit) {

    val navigationTabs = listOf(Routes.MAP_SCREEN to "Map", Routes.PUBS_LIST_SCREEN to "Pubs List")

    val selectedIndex = when {
        currentRoute == Routes.PUBS_LIST_SCREEN -> 1
        else -> 0
    }

    TabRow(selectedTabIndex = selectedIndex) {
        navigationTabs.forEachIndexed { index, (route, label) ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelect(route) },
                text = { Text(label) }
            )
        }
    }
}

