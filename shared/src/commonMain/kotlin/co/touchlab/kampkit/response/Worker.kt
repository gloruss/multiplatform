package co.touchlab.kampkit.response

import kotlinx.serialization.Serializable

@Serializable
data class Worker(
    val id : Int,
    val name : String,
    val uuid : String
)