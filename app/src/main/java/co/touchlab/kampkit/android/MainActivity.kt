package co.touchlab.kampkit.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kampkit.android.ui.MainScreen
import co.touchlab.kampkit.android.ui.WorkersScreen
import co.touchlab.kampkit.android.ui.theme.KaMPKitTheme
import co.touchlab.kampkit.injectLogger
import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val log: Logger by injectLogger("MainActivity")
    private val userViewModel : UserViewModel by viewModel()
    private val workerViewModel : WorkerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaMPKitTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main" ){
                    composable("main"){
                        MainScreen( userViewModel, navController,log)
                    }
                    composable("workers"){
                        WorkersScreen(viewModel = workerViewModel, log = log )
                    }
                }

            }
        }

    }
}
