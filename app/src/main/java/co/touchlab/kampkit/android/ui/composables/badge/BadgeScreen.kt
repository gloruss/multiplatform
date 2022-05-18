package co.touchlab.kampkit.android.ui.composables.badge

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kampkit.android.ui.composables.Loader
import co.touchlab.kampkit.android.ui.viewmodel.BadgeReport
import co.touchlab.kampkit.android.ui.viewmodel.BadgeReportViewModel
import co.touchlab.kampkit.models.DataState
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BadgeScreen(workerUuid: String, name: String) {
    val viewModel = getViewModel<BadgeReportViewModel>(parameters = { parametersOf(workerUuid) })
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareBadgeFlow = remember(viewModel.badgeFlow, lifecycleOwner) {
        viewModel.badgeFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val badgeState by lifecycleAwareBadgeFlow.collectAsState(initial = viewModel.badgeFlow.value)
    BadgeScreenContent(badgeState = badgeState, onDateChanged = { day, month, year ->
        viewModel.setCalendar(day, month, year)
    }, onDayChanged = { viewModel.scrollCalendar(it) }, name)
}

@SuppressLint("SimpleDateFormat")
@Composable
fun BadgeScreenContent(
    badgeState: DataState<BadgeReport>,
    onDateChanged: (Int, Int, Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    name: String
) {

    val date = badgeState.data?.date
    val badge = badgeState.data?.badge
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = name, fontSize = 26.sp)
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = date ?: "Not available", fontSize = 24.sp)
        Spacer(modifier = Modifier.padding(16.dp))
        if (badgeState.loading) {
            Loader()
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "back", modifier = Modifier
                    .clickable {
                        onDayChanged(-1)
                    })
                Column() {
                    if (badge != null) {
                        Text(text = "Start at : ${badge.start.split("T").get(1)}")
                        Spacer(modifier = Modifier.padding(8.dp))
                        //TODO("WORKAROUND FIX \"null\" in API")
                        val end = if (badge.end.equals("null")) null else badge.end
                        Text(text = "End at : ${end?.split("T")?.get(1) ?: "Not ended"}")
                    } else {
                        Text(text = badgeState.exception ?: "No Data")
                    }
                }
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "forward",
                    modifier = Modifier.clickable {
                        onDayChanged(1)
                    })
            }
        }

    }
}