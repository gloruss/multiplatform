package co.touchlab.kampkit.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.worker.request.WorkerRequest
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.WorkersModel
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat

class WorkerViewModel : ViewModel(), KoinComponent {

    private val log: Logger by injectLogger("WorkerViewModel")
    private val scope = viewModelScope
    private val workerModel by inject<WorkersModel>()
    private val _workersFlow : MutableStateFlow<DataState<List<Worker>>> = MutableStateFlow(DataState(loading = true, empty = true))
    val workersFlow: StateFlow<DataState<List<Worker>>> = _workersFlow

    init {
        observeWorkers()
    }



     fun observeWorkers(){
        scope.launch {
            log.v("Observing Worker")
            flowOf(
                workerModel.refreshWorkersIfStale(true),
                workerModel.getWorkersFromCache()
            ).flattenMerge().collect{
                datastate ->
                if(datastate.loading){
                    val temp = _workersFlow.value.copy(loading = true)
                    _workersFlow.value = temp
                }
                else{
                    _workersFlow.value = datastate
                }
            }
        }
    }


    fun refreshWorkers(forced : Boolean = false){
        scope.launch {
            log.d{"refresh workers"}
            workerModel.refreshWorkersIfStale(forced).collect{
                datastate ->
                if(datastate.loading){
                    val temp = _workersFlow.value.copy(loading = true)
                    _workersFlow.value = temp
                }
                else{
                    _workersFlow.value = datastate
                }
            }

        }
    }


    fun saveWorker(name : String){
        scope.launch {
            log.d{"save Worker"}
            workerModel.insertWorker(WorkerRequest(name,"123456qwer"))
        }
    }


}