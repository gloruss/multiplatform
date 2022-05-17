package co.touchlab.kampkit.ktor.worker.request

import kotlinx.serialization.Serializable

@Serializable
data class WorkerRequest(
    val name : String,
    val uuid : String? = null
)