package co.touchlab.kampkit.models

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.worker.WorkerApi
import co.touchlab.kampkit.response.User
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WorkersModel : KoinComponent {

    private val dbHelper: DatabaseHelper by inject()
    private val workerApi : WorkerApi by inject()
    private val log: Logger by injectLogger("WorkerModel")


    suspend fun getWorkerFromNetwork() : DataState<List<Worker>>{
        return try {
            val response = workerApi.getWorkers()
            DataState(data = response)
        }
        catch (exception : Exception){
            log.e(exception) { "Error get workers" }
            DataState(exception = "Unable get workers")
        }
    }

}