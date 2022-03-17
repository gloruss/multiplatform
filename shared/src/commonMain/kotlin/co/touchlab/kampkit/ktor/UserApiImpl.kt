package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.FIREBASE_AUTH_DELETE
import co.touchlab.kampkit.FIREBASE_AUTH_REFRESH
import co.touchlab.kampkit.FIREBASE_AUTH_SIGNIN
import co.touchlab.kampkit.FIREBASE_AUTH_SIGNUP
import co.touchlab.kampkit.response.User
import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.call.body
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitForm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UserApiImpl(private val log: Logger,val  engine: HttpClientEngine) : UserApi {

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


    }

    init {
        ensureNeverFrozen()
    }


    @Serializable
    data class SignInRequest(val email: String, val password: String, val returnSecureToken : Boolean)

    override suspend fun signUp(email: String, password: String): User {
         return httpClient.post(FIREBASE_AUTH_SIGNUP) {
            contentType(ContentType.Application.Json)
            setBody(SignInRequest(email, password,true))
        }.body<User>()
    }

    override suspend fun login(email: String, password: String): User {
        return httpClient.post(FIREBASE_AUTH_SIGNIN) {
            contentType(ContentType.Application.Json)
            val request = SignInRequest(email, password,true)
            setBody(request)
        }.body<User>()
    }

    @Serializable
    data class RefreshRequest(val refreshToken: String, @SerialName("grant_type")val grantType: String = "refresh_token")

    override suspend fun refresh(refreshToken: String): User {
        return httpClient.post(FIREBASE_AUTH_REFRESH){
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken))
        }.body()
    }

    @Serializable
    data class DeleteAccountRequest(val idToken: String)

    override suspend fun deleteUser(idToken: String): HttpStatusCode {
        return httpClient.post(FIREBASE_AUTH_DELETE){
            contentType(ContentType.Application.Json)
            setBody(DeleteAccountRequest(idToken))
        }.body()
    }
}