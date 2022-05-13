package co.touchlab.kampkit.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import co.touchlab.kampkit.AppInfo
import co.touchlab.kampkit.android.ui.viewmodel.BadgeViewModel
import co.touchlab.kampkit.android.ui.viewmodel.BreedViewModel
import co.touchlab.kampkit.android.ui.viewmodel.UserViewModel
import co.touchlab.kampkit.android.ui.viewmodel.WorkerViewModel
import co.touchlab.kampkit.initKoin
import co.touchlab.kampkit.models.WorkersModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                viewModel { BreedViewModel() }
                viewModel { UserViewModel() }
                viewModel { WorkerViewModel() }
                viewModel{ BadgeViewModel()}
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("KAMPSTARTER_SETTINGS", Context.MODE_PRIVATE)
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
                single{WorkersModel()}
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
