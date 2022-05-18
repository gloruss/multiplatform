package co.touchlab.kampkit.ktor.badge

import co.touchlab.kampkit.BADGE_URL
import co.touchlab.kampkit.BASE_URL
import co.touchlab.kampkit.QUERY_PARAM_DATE
import co.touchlab.kampkit.QUERY_PARAM_UUID
import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.response.Badge
import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

class BadgeApiImpl(private val client : HttpClient) : BadgeApi{

    init {
        ensureNeverFrozen()
    }

    override suspend fun insertBadge(badge: BadgeRequest): Badge {
        return client.post {
            badge(BADGE_URL)
            setBody(badge)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body()
    }

    override suspend fun getBadge(badge: BadgeRequest): Badge {
        return client.get{
            badge(BADGE_URL)
            parameter(QUERY_PARAM_UUID,badge.worker_uuid)
            parameter(QUERY_PARAM_DATE,badge.time)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body()
    }

    private fun HttpRequestBuilder.badge(path: String) {
        url {
            takeFrom(BASE_URL)
            encodedPath = path
        }
    }
}