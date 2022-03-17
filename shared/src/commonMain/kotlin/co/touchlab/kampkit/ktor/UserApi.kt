package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.FIREBASE_AUTH_SIGNUP
import co.touchlab.kampkit.response.User
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable


interface UserApi {

    @Serializable
    data class SignInRequest(val email: String, val password: String, val returnSecureToken : Boolean)

    suspend fun signUp(email: String, password: String): User

    suspend fun login(email: String, password: String) : User

    suspend fun refresh(refreshToken : String) : User

    suspend fun deleteUser(idToken : String) : HttpStatusCode
}