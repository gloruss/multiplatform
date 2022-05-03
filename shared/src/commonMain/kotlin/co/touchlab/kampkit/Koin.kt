package co.touchlab.kampkit

import co.touchlab.kampkit.ktor.DogApi
import co.touchlab.kampkit.ktor.DogApiImpl
import co.touchlab.kampkit.ktor.UserApi
import co.touchlab.kampkit.ktor.UserApiImpl
import co.touchlab.kampkit.ktor.badge.BadgeApi
import co.touchlab.kampkit.ktor.badge.BadgeApiImpl
import co.touchlab.kampkit.ktor.worker.WorkerApi
import co.touchlab.kampkit.ktor.worker.WorkerApiImpl
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule
        )
    }

    // Dummy initialization logic, making use of appModule declarations for demonstration purposes.
    val koin = koinApplication.koin
    // doOnStartup is a lambda which is implemented in Swift on iOS side
    val doOnStartup = koin.get<() -> Unit>()
    doOnStartup.invoke()

    val kermit = koin.get<Logger> { parametersOf(null) }
    // AppInfo is a Kotlin interface with separate Android and iOS implementations
    val appInfo = koin.get<AppInfo>()
    kermit.v { "App Id ${appInfo.appId}" }

    return koinApplication
}

@OptIn(InternalAPI::class)
private val coreModule = module {
    single {
        DatabaseHelper(
            get(),
            getWith("DatabaseHelper"),
            Dispatchers.Default
        )
    }
    single<DogApi> {
        DogApiImpl(
            getWith("DogApiImpl"),
            get()
        )
    }
    single<UserApi> {
        UserApiImpl(
            getWith("UserApiImpl"),
            get()
        )
    }

    single<WorkerApi> {
        WorkerApiImpl(
            getWith("WorkerApiImpl"),
             get(),
            get()
        )
    }

    single<BadgeApi>{
        BadgeApiImpl(get())
    }

    single<Clock> {
        Clock.System
    }

    fun provideHttpClient(engine: HttpClientEngine, dbHelper: DatabaseHelper, log: Logger) : HttpClient =
        HttpClient(engine){
            install(ContentNegotiation) {
                json(Json{ ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        log.v { message }
                    }
                }

                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                val timeout = 30000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }

            install(Auth){
                lateinit var tokenInfo: WorkerApiImpl.TokenInfo
                var refreshTokenInfo: WorkerApiImpl.TokenInfo

                bearer {
                    val tokenClient = HttpClient(engine){
                        install(ContentNegotiation) {
                            json(Json{ ignoreUnknownKeys = true })
                        }
                    }
                    loadTokens {

                        val user =  dbHelper.getUser()
                        BearerTokens(user.idToken,user.refreshToken)

                    }
                    refreshTokens {
                        val user =  dbHelper.getUser()
                        refreshTokenInfo = tokenClient.submitForm(
                            url = FIREBASE_AUTH_REFRESH,
                            formParameters = Parameters.build {
                                append("grant_type","refresh_token")
                                append("refresh_token",user.refreshToken)
                            }
                        ).body()
                        BearerTokens(refreshTokenInfo.idToken,refreshTokenInfo.refreshToken)

                    }
                }
            }
        }


    single {
        provideHttpClient(get(),get(),getWith("Httpclient"))
    }

    // platformLogWriter() is a relatively simple config option, useful for local debugging. For production
    // uses you *may* want to have a more robust configuration from the native platform. In KaMP Kit,
    // that would likely go into platformModule expect/actual.
    // See https://github.com/touchlab/Kermit
    val baseLogger = Logger(config = StaticConfig(logWriterList = listOf(platformLogWriter())), "KampKit")
    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

// Simple function to clean up the syntax a bit
fun KoinComponent.injectLogger(tag: String): Lazy<Logger> = inject { parametersOf(tag) }

expect val platformModule: Module
