package co.touchlab.kampkit

import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kampkit.db.User
import co.touchlab.kampkit.db.Worker
import co.touchlab.kampkit.sqldelight.transactionWithContext
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: KaMPKitDb = KaMPKitDb(sqlDriver)

    fun selectAllItems(): Flow<List<Breed>> =
        dbRef.tableQueries
            .selectAll()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    fun selectAllWorkers() : Flow<List<Worker>> =
        dbRef.tableQueries
            .selectAllWorker()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)



    suspend fun insertUser(user: co.touchlab.kampkit.response.User){
        log.d { "Inserting ${user.email}  into database" }
        dbRef.transactionWithContext(backgroundDispatcher){
            dbRef.tableQueries.insertUser(user.idToken,user.email,user.refreshToken,user.uid)
        }
    }

    suspend fun selectById(id: Long): Flow<List<Breed>> =
        dbRef.tableQueries
            .selectById(id)
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun deleteAll() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.deleteAll()
        }
    }




     fun selectUser() : Flow<List<User>> =
        dbRef.tableQueries
            .selectAllUser()
            .asFlow().mapToList()
            .flowOn(backgroundDispatcher)


    fun getUser() : User = dbRef.tableQueries.selectAllUser().executeAsList().first()




     fun getWorker(uuid : String) : Worker? =
        dbRef.tableQueries
            .selectWorkerById(uuid)
            .executeAsOneOrNull()







    suspend fun insertWorkers(workers : List<Worker>){
        log.d { "Inserting ${workers.size} workers into database" }
        dbRef.transactionWithContext(backgroundDispatcher){
            workers.forEach {
                worker ->
                dbRef.tableQueries.insertWorker(worker.id,worker.name,worker.uuid)
            }
        }
    }




}


