package co.touchlab.kampkit.android.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.android.ui.Error
import co.touchlab.kampkit.android.ui.WorkerList
import co.touchlab.kampkit.android.ui.viewmodel.WorkerViewModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@Composable
fun WorkersScreen(
){
    val viewModel = getViewModel<WorkerViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWorkesFlow = remember(viewModel.workersFlow,lifecycleOwner){
        viewModel.workersFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    @SuppressLint("StateFlowValueCalledInComposition")
    val workersState by lifecycleAwareWorkesFlow.collectAsState(initial = viewModel.workersFlow.value)

    WorkerScreenContent(workersState = workersState, onRefresh = {viewModel.refreshWorkers(true)}, onConfirm = {viewModel.saveWorker(it)})
}

@Composable
fun WorkerScreenContent(
    workersState : DataState<List<Worker>>,
    onRefresh: () -> Unit = {},
    onConfirm : (String) -> Unit, ){

    val showDialog = remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = workersState.loading),
            onRefresh = onRefresh
        ){

            Scaffold(
                topBar ={TopAppBar(
                    contentColor = Color.White,
                    content = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(id = R.string.worker_list), fontSize = 24.sp )
                                IconButton(onClick = { showDialog.value = true },) {
                                    Icon(Icons.Filled.Add,"" )
                                }
                            }
                    }
                ) }
            ) {
                innerPadding ->
                Box(){
                    if(showDialog.value){
                        AlertDialog(onDismissRequest = { showDialog.value = false},
                            title = {
                                Text(text = stringResource(id = R.string.create_worker_title))
                            },
                            text = { TextField(value = name , onValueChange = {name = it}) },
                            buttons = {
                                Row(
                                    modifier = Modifier.padding(all = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            if(name.isNotEmpty()){
                                                onConfirm(name)
                                            }
                                            showDialog.value = false }
                                    ) {
                                        Text(stringResource(id = R.string.confirm))
                                    }
                                }
                            }
                        )
                    }

                    if(workersState.loading){
                        Loader()
                    }
                    val data = workersState.data

                    if(data != null){
                        WorkerList(workers = data)
                    }
                    val exception = workersState.exception
                    if(exception != null){
                        Error(exception)
                    }
                }
            }


        }



}