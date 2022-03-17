package co.touchlab.kampkit.ktor.worker

import co.touchlab.kampkit.response.Worker

interface WorkerApi {

    suspend fun getWorkers() : List<Worker>

}