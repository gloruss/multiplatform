package co.touchlab.kampkit.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("idToken") val idToken : String,
    @SerialName("email") val email : String,
    @SerialName("refreshToken") val refreshToken : String,
    @SerialName("localId") val uid : String,
    @SerialName("expiresIn") val expiresIn : Int
)
