package co.touchlab.kampkit.models

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.badge.BadgeApi
import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.response.Badge
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BadgeModel : KoinComponent {

    private val log: Logger by injectLogger("BadgeModel")
    private val api : BadgeApi by inject()
    private val dbHelper: DatabaseHelper by inject()



    suspend fun badge(badgeRequest: BadgeRequest) : DataState<Badge>{
        return try {

            val result = api.insertBadge(badgeRequest)
            DataState(result, empty = false)
        }
        catch (exception : Exception){
            log.e(exception) { "Error badge worker" }
            DataState(exception = "Unable badge worker",empty = false)
        }
    }


}