package co.touchlab.kampkit.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kampkit.android.ui.composables.LoginScreen
import co.touchlab.kampkit.android.ui.composables.MainScreen
import co.touchlab.kampkit.android.ui.composables.QrCodeScannerScreen
import co.touchlab.kampkit.android.ui.composables.WorkersScreen
import co.touchlab.kampkit.android.ui.composables.navigation.LOGIN_ROUTE
import co.touchlab.kampkit.android.ui.composables.navigation.MAIN_ROUTE
import co.touchlab.kampkit.android.ui.theme.KaMPKitTheme
import co.touchlab.kampkit.android.ui.viewmodel.BadgeViewModel
import co.touchlab.kampkit.android.ui.viewmodel.UserViewModel
import co.touchlab.kampkit.android.ui.viewmodel.WorkerViewModel
import co.touchlab.kampkit.injectLogger
import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent





class MainActivity : ComponentActivity(), KoinComponent {

    private val log: Logger by injectLogger("MainActivity")
    private val userViewModel : UserViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaMPKitTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = if (!userViewModel.isLoggedIn.value) LOGIN_ROUTE else MAIN_ROUTE ){
                    composable(LOGIN_ROUTE){
                        LoginScreen( userViewModel, navController,log)
                    }
                    composable(MAIN_ROUTE){
                        MainScreen()
                    }

                }

            }
        }

    }
}
