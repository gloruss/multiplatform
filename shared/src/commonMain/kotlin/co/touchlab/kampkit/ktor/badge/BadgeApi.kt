package co.touchlab.kampkit.ktor.badge

import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.response.Badge

interface BadgeApi {

    suspend fun insertBadge(badge : BadgeRequest) : Badge

}