package co.touchlab.kampkit.android.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kampkit.android.ui.composables.navigation.BottomNavigationBar
import co.touchlab.kampkit.android.ui.composables.navigation.NavRoutes

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    Scaffold(
        content = {innerPadding -> Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())){
            MainNavigationHost(navController = navController)
        } },
        bottomBar = { BottomNavigationBar(navController = navController)}
    )
}

@Composable
fun MainNavigationHost(navController: NavHostController){

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Workers.route,
    ) {
        composable(NavRoutes.Workers.route) {
            WorkersScreen()
        }

        composable(NavRoutes.Scanner.route) {
            QrCodeScannerScreen()
        }

    }
}




@Composable
fun Loader(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .semantics { contentDescription = "loader" },
        )
        Text(text = "Loading..")
    }
}

