package co.touchlab.kampkit.response

import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val id : Int,
    val start : String,
    val end : String?,
    val hours : Int,
    val worker_uuid : String,
    val worker_id : String?,
    val type : String
)
