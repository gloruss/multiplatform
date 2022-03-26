package co.touchlab.kampkit.ktor.request

import kotlinx.serialization.Serializable

@Serializable
data class WorkerRequest(
    val name : String,
    val uuid : String
)