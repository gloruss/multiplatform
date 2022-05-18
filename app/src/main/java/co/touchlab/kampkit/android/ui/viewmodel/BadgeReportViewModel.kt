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
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BadgeReportViewModel(
    private val workerUuid : String
) : ViewModel(), KoinComponent {

    private val badgeModel by inject<BadgeModel>()
    private val log: Logger by injectLogger("BadgeReportViewModel")
    private val scope = viewModelScope
    var calendar = Calendar.getInstance()
    val printableDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val _badgeFlow : MutableStateFlow<DataState<BadgeReport>> = MutableStateFlow(
        DataState(loading = false, data = BadgeReport(date = printableDateFormat.format(calendar.time) ))
    )
    val badgeFlow: StateFlow<DataState<BadgeReport>> = _badgeFlow
    var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())




    init {
        getBadge(workerUuid, calendar.time)
    }


    fun getBadge(uuid : String, date: Date){
        val dateString = printableDateFormat.format(date)
        _badgeFlow.value = DataState(data = BadgeReport(date = dateString),loading = true)
        scope.launch {
            val response = badgeModel.getBadge(badgeRequest = BadgeRequest(sdf.format(date),uuid))
            if(response.data != null){
                _badgeFlow.value = DataState(BadgeReport(date =dateString , response.data),loading = false)
            }
           else{
               _badgeFlow.value = DataState(BadgeReport(date = dateString), loading = false, exception = "No data for the date requested")
            }
        }

    }


     fun setCalendar(day : Int, month : Int, year : Int){
        calendar.set(year,month,day)
        getBadge(workerUuid,calendar.time)
    }


    fun scrollCalendar(dayToAdd : Int){
        calendar.roll(Calendar.DAY_OF_YEAR,dayToAdd)
        getBadge(workerUuid,calendar.time)
    }
}


data class BadgeReport(
    val date : String,
    val badge : Badge? = null
)