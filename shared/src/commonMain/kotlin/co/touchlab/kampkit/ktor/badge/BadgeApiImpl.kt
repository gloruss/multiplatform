package co.touchlab.kampkit.ktor.badge

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.response.Badge
import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
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
            badge("test/badge")
            setBody(badge)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body()
    }


    private fun HttpRequestBuilder.badge(path: String) {
        url {
            takeFrom("https://nasone.herokuapp.com/")
            encodedPath = path
        }
    }
}