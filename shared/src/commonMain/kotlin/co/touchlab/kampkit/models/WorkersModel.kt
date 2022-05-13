package co.touchlab.kampkit.models

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.worker.request.WorkerRequest
import co.touchlab.kampkit.ktor.worker.WorkerApi
import co.touchlab.kampkit.response.Worker
import co.touchlab.kampkit.response.toDB
import co.touchlab.kampkit.response.toObject
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WorkersModel : KoinComponent {
    private val clock: Clock by inject()
    private val settings: Settings by inject()
    private val dbHelper: DatabaseHelper by inject()
    private val workerApi: WorkerApi by inject()
    private val log: Logger by injectLogger("WorkerModel")

    companion object {
        internal const val DB_TIMESTAMP_KEY_WORKERS = "DbTimestampKeyWorkers"
    }

    fun refreshWorkersIfStale(forced: Boolean = false): Flow<DataState<List<Worker>>> = flow {
        emit(DataState(loading = true))
        val currentTimeMS = clock.now().toEpochMilliseconds()
        val stale = isWorkersListStale(currentTimeMS)
        val networkWorkers: DataState<List<Worker>>
        if (stale || forced) {
            networkWorkers = getWorkerFromNetwork(currentTimeMS)
            if (networkWorkers.data != null) {
                dbHelper.insertWorkers(networkWorkers.data.map { worker -> worker.toDB() })
            } else {
                emit(networkWorkers)
            }
        }
    }

    suspend fun getWorkerFromNetwork(currentTimeMS: Long): DataState<List<Worker>> {
        return try {
            val response = workerApi.getWorkers()
            settings.putLong(DB_TIMESTAMP_KEY_WORKERS, currentTimeMS)
            DataState(data = response, empty = false)
        } catch (exception: Exception) {
            log.e(exception) { "Error get workers" }
            DataState(exception = "Unable get workers", empty = false)
        }
    }

    private fun isWorkersListStale(currentTimeMS: Long): Boolean {
        val lastDownloadTimeMS = settings.getLong(DB_TIMESTAMP_KEY_WORKERS, 0)
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < currentTimeMS
        if (!stale) {
            log.i { "Workers not fetched from network. Recently updated" }
        }
        return stale
    }

    suspend fun getWorkersFromCache(): Flow<DataState<List<Worker>>> =
        dbHelper.selectAllWorkers()
            .mapNotNull { list ->
                if (list.isEmpty())
                    null
                else {
                    DataState(data = list.map { worker ->
                        Worker(
                            worker.id,
                            worker.name,
                            worker.uuid
                        )
                    })
                }
            }

    suspend fun insertWorker(worker: WorkerRequest): DataState<Worker> {
        return try {
            val response = workerApi.saveWorker(worker)
            dbHelper.insertWorkers(listOf(response.toDB()))
            DataState(response, empty = false)
        } catch (exception: Exception) {
            log.e(exception) { "Error insert workers" }
            DataState(exception = "Unable insert workers", empty = false)
        }
    }

    fun getWorkerById(workerUID: String): Worker? {
        return dbHelper.getWorker(workerUID)?.run {
            this.toObject()
        }
    }
}