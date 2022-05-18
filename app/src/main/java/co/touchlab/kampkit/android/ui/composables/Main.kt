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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.touchlab.kampkit.android.WORKERUUID_PARAM
import co.touchlab.kampkit.android.WORKER_NAME_PARAM
import co.touchlab.kampkit.android.ui.composables.badge.BadgeScreen
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
            WorkerNavigationHost()
        }

        composable(NavRoutes.Scanner.route) {
            QrCodeScannerScreen()
        }

    }
}


@Composable
fun WorkerNavigationHost(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.WorkersList.route){
        composable(NavRoutes.WorkersList.route){
            WorkersScreen(navController)
        }
        composable(NavRoutes.Report.route + "/{$WORKERUUID_PARAM}/{$WORKER_NAME_PARAM}", arguments = listOf(navArgument(WORKERUUID_PARAM) { type = NavType.StringType },
            navArgument(WORKER_NAME_PARAM) { type = NavType.StringType })
        ){
            entry ->
            val workeruuid = entry.arguments?.getString(WORKERUUID_PARAM) ?: ""
            val name = entry.arguments?.getString(WORKER_NAME_PARAM) ?: ""
            BadgeScreen(workerUuid = workeruuid,name = name)
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

