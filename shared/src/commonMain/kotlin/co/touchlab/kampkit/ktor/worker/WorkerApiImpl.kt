package co.touchlab.kampkit.ktor.worker

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.FIREBASE_AUTH_REFRESH
import co.touchlab.kampkit.FIREBASE_AUTH_SIGNIN
import co.touchlab.kampkit.response.User
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WorkerApiImpl(private val log: Logger, engine: HttpClientEngine, dbHelper: DatabaseHelper) : WorkerApi {

    private val httpClient = HttpClient(engine) {
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
            lateinit var tokenInfo: TokenInfo
            var refreshTokenInfo: TokenInfo

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

    init {
        ensureNeverFrozen()
    }


    override suspend fun getWorkers(): List<Worker> {
        TODO("Not yet implemented")
    }


    @Serializable
    data class TokenInfo(
        @SerialName("user_id") val user_id : String,
        @SerialName("expires_in") val expiresIn: Int,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("token_type") val tokenType: String,
        @SerialName("id_token") val idToken: String,
    )
}