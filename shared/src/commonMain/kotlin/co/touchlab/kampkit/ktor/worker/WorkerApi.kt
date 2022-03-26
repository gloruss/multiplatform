package co.touchlab.kampkit.ktor.worker

import co.touchlab.kampkit.ktor.request.WorkerRequest
import co.touchlab.kampkit.response.Worker

interface WorkerApi {

    suspend fun getWorkers() : List<Worker>

    suspend fun saveWorker(worker: WorkerRequest) : Worker

}