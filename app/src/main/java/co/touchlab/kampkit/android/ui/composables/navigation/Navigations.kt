package co.touchlab.kampkit.android.ui.composables.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

const val MAIN_ROUTE = "main"
const val WORKERS_ROUTE = "workers"
const val SCANNER_ROUTE ="qr_scanner"
const val LOGIN_ROUTE = "login"




sealed class NavRoutes(val route: String) {
    object Main : NavRoutes(MAIN_ROUTE)
    object Workers : NavRoutes(WORKERS_ROUTE)
    object Scanner : NavRoutes(SCANNER_ROUTE)
    object Login : NavRoutes(LOGIN_ROUTE)
}


data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Workers",
            image = Icons.Filled.Person,
            route = WORKERS_ROUTE
        ),
        BarItem(
            title = "Badge",
            image = Icons.Filled.Check,
            route = SCANNER_ROUTE
        ),

    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    BottomNavigation {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->

            BottomNavigationItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                },
            )
        }
    }
}