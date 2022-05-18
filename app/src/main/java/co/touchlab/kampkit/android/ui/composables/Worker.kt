package co.touchlab.kampkit.android.ui.composables

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.android.ui.Error
import co.touchlab.kampkit.android.ui.composables.navigation.NavRoutes
import co.touchlab.kampkit.android.ui.shareBitmap
import co.touchlab.kampkit.android.ui.viewmodel.WorkerViewModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.response.Worker
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@Composable
fun WorkersScreen(
    navHostController: NavHostController
) {
    val viewModel = getViewModel<WorkerViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWorkesFlow = remember(viewModel.workersFlow, lifecycleOwner) {
        viewModel.workersFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    val workersState by lifecycleAwareWorkesFlow.collectAsState(initial = viewModel.workersFlow.value)

    WorkerScreenContent(
        workersState = workersState,
        onRefresh = { viewModel.refreshWorkers(true) },
        onConfirm = { viewModel.saveWorker(it) },
        onDeleteWorker = { viewModel.deleteWorker(it) },
    onItemClicked = { uiid, name ->
        navHostController.navigate("${NavRoutes.Report.route}/$uiid/$name")})
}

@Composable
fun WorkerScreenContent(
    workersState: DataState<List<Worker>>,
    onRefresh: () -> Unit = {},
    onConfirm: (String) -> Unit,
    onDeleteWorker: (Worker) -> Unit,
    onItemClicked: (String,String) -> Unit
) {

    val showDialog = remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = workersState.loading),
        onRefresh = onRefresh
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    contentColor = Color.White,
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(id = R.string.worker_list), fontSize = 24.sp)
                            IconButton(onClick = { showDialog.value = true }) {
                                Icon(Icons.Filled.Add, "")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box() {
                if (showDialog.value) {
                    AlertDialog(onDismissRequest = { showDialog.value = false },
                        title = {
                            Text(text = stringResource(id = R.string.create_worker_title))
                        },
                        text = { TextField(value = name, onValueChange = { name = it }) },
                        buttons = {
                            Row(
                                modifier = Modifier.padding(all = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        if (name.isNotEmpty()) {
                                            onConfirm(name)
                                        }
                                        showDialog.value = false
                                    }
                                ) {
                                    Text(stringResource(id = R.string.confirm))
                                }
                            }
                        }
                    )
                }

                if (workersState.loading) {
                    Loader()
                }
                val data = workersState.data

                if (data != null) {
                    WorkerList(workers = data, onDeleteWorker,onItemClicked)
                }
                val exception = workersState.exception
                if (exception != null) {
                    Error(exception)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkerList(workers: List<Worker>, onWorkerDelete: (Worker) -> Unit, onItemClicked: (String,String) -> Unit) {
    LazyColumn {
        items(workers, { workers: Worker -> workers.uuid }) { item ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onWorkerDelete(item)
            }
            WorkerRow(worker = item, dismissState, onItemClicked = onItemClicked)
            Divider()
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkerRow(worker: Worker, dismissState: DismissState, onItemClicked : (String,String) -> Unit) {
    val bitmap = getQrCodeBitmap(worker.uuid)
    val context = LocalContext.current
    SwipeToDismiss(state = dismissState, background = {
        val color by animateColorAsState(
            when (dismissState.targetValue) {
                DismissValue.Default -> Color.White
                else -> Color.Red
            }
        )
        val alignment = Alignment.CenterEnd
        val icon = Icons.Default.Delete

        val scale by animateFloatAsState(
            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(color)
                .padding(horizontal = Dp(20f)),
            contentAlignment = alignment
        ) {
            Icon(
                icon,
                contentDescription = "Delete Icon",
                modifier = Modifier.scale(scale)
            )
        }
    }, modifier = Modifier.padding(10.dp), directions = setOf(
        DismissDirection.EndToStart
    ), dismissThresholds = { direction ->
        FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
    }, dismissContent = {
        Row(modifier = Modifier.fillMaxSize().clickable {
            onItemClicked(worker.uuid,worker.name)
        }) {
            Text(text = worker.name, Modifier.weight(1F))
            Spacer(Modifier.weight(1f))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Qrcode",
                modifier = Modifier.clickable {
                    shareBitmap(
                        bitmap,
                        context
                    )
                })
        }

    })
}