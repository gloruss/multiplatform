package co.touchlab.kampkit.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.models.BadgeModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.WorkersModel
import co.touchlab.kampkit.response.Badge
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BadgeViewModel : ViewModel(), KoinComponent {

    private val log: Logger by injectLogger("BadgeViewModel")
    private val scope = viewModelScope
    private val badgeModel by inject<BadgeModel>()
    private val workersModel by inject<WorkersModel>()
    private val _badgeFlow : MutableStateFlow<DataState<Badge>> = MutableStateFlow(
        DataState(loading = false, empty = true)
    )
    val badgeFlow: StateFlow<DataState<Badge>> = _badgeFlow
    var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALY)
    var badging = false




    fun badge(workerUID : String){
        if(!badging){
            badging = true
            scope.launch {
               val worker = workersModel.getWorkerById(workerUID)
                if(worker == null){
                    _badgeFlow.value = DataState(exception = "Worker not found in database", empty = false)
                }
                else{
                    val time = sdf.format(Date())
                    val result = badgeModel.badge(BadgeRequest(time,workerUID)).copy(empty = false)
                    _badgeFlow.value = result
                }
                badging = false
            }
        }

    }

    fun badgeResultViewed(){
        _badgeFlow.value = DataState(loading = false, empty = true)
    }



}