package co.touchlab.kampkit.ktor.badge.request

import kotlinx.serialization.Serializable

@Serializable
data class BadgeRequest(
    val time : String,
    val worker_uuid : String,

)
