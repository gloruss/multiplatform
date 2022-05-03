package co.touchlab.kampkit.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.badge.request.BadgeRequest
import co.touchlab.kampkit.models.BadgeModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.response.Badge
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BadgeViewModel : ViewModel(), KoinComponent {

    private val log: Logger by injectLogger("BadgeViewModel")
    private val scope = viewModelScope
    private val badgeModel = BadgeModel()
    private val _badgeFlow : MutableStateFlow<DataState<Badge>> = MutableStateFlow(
        DataState(loading = false, empty = true)
    )
    val badgeFlow: StateFlow<DataState<Badge>> = _badgeFlow
    var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALY)


    fun badge(workerUID : String){
        scope.launch {
            val time = sdf.format(Date())
            val result = badgeModel.badge(BadgeRequest(time,workerUID))
            _badgeFlow.value = result
        }
    }

    fun badgeResultViewed(){
        _badgeFlow.value = DataState(loading = false, empty = true)
    }

}