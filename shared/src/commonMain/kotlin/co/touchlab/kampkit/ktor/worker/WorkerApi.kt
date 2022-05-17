package co.touchlab.kampkit.ktor.worker

import co.touchlab.kampkit.ktor.worker.request.WorkerRequest
import co.touchlab.kampkit.response.Worker
import io.ktor.client.statement.HttpResponse
import io.ktor.http.cio.Response

interface WorkerApi {

    suspend fun getWorkers() : List<Worker>

    suspend fun saveWorker(worker: WorkerRequest) : Worker

    suspend fun deleteWorker(worker: WorkerRequest) : Int

}