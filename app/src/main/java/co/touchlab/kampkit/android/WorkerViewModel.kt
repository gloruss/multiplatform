package co.touchlab.kampkit.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.WorkersModel
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class WorkerViewModel : ViewModel(), KoinComponent {

    private val log: Logger by injectLogger("WorkerViewModel")
    private val scope = viewModelScope
    private val workerModel = WorkersModel()
    private val _workersFlow : MutableStateFlow<DataState<List<Worker>>> = MutableStateFlow(DataState(loading = true))
    val workersFlow: StateFlow<DataState<List<Worker>>> = _workersFlow





     fun observeWorkers(){
        scope.launch {
            log.v("Observing Worker")
            val data = workerModel.getWorkerFromNetwork()
            _workersFlow.value = data
        }
    }


}