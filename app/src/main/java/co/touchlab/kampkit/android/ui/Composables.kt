package co.touchlab.kampkit.android.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.android.UserViewModel
import co.touchlab.kampkit.android.WorkerViewModel
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.ItemDataSummary
import co.touchlab.kampkit.response.Worker
import co.touchlab.kermit.Logger
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    navController: NavController,
    log: Logger
) {

    val lifecycleOwner = LocalLifecycleOwner.current


    val lifecycleAwareUserFlow = remember(userViewModel.userFlow,lifecycleOwner){
        userViewModel.userFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }



    @SuppressLint("StateFlowValueCalledInComposition")
    val userState by lifecycleAwareUserFlow.collectAsState(userViewModel.userFlow.value)

    LaunchedEffect(key1 = Unit ){
        userViewModel.userFlow.collect{
            userState->
            if(!userState.empty){
                navController.navigate("workers")

            }

        }
    }
    UserScreenContent(userState = userState, onLoginClick = {email, password ->userViewModel.login(email,password)}, )


    /*MainScreenContent(
        dogsState = dogsState,
        onRefresh = { viewModel.refreshBreeds(true) },
        onSuccess = { data -> log.v { "View updating with ${data.allItems.size} breeds" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
        onFavorite = { viewModel.updateBreedFavorite(it) }
    )*/





}

@Composable
fun WorkerRoute(){

}



@Composable
fun WorkersScreen(
    viewModel: WorkerViewModel,
    log: Logger
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWorkesFlow = remember(viewModel.workersFlow,lifecycleOwner){
        viewModel.workersFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    @SuppressLint("StateFlowValueCalledInComposition")
    val workersState by lifecycleAwareWorkesFlow.collectAsState(initial = viewModel.workersFlow.value)

   WorkerScreenContent(workersState = workersState, onRefresh = {viewModel.refreshWorkers(true)}, onConfirm = {viewModel.saveWorker(it)})
}



@Composable
fun UserScreenContent(
    userState : DataState<co.touchlab.kampkit.response.User>,
    onLoginClick: (String, String) -> Unit
){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ){
        if(userState.empty){
            LoginFields(
                email = email,
                password = password,
                onEmailChange = {
                    email = it
                },
                onPasswordChange = {
                    password = it
                },
                onLoginClick,
                onSignUpClick = {
                        email,password ->

                }
            )
        }
        else{


            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(id = R.string.welcome,userState.data?.email ?: "trmon"))
                val uid = userState.data?.uid
                if(!uid.isNullOrEmpty()){
                    Image(bitmap = getQrCodeBitmap(uid).asImageBitmap(), contentDescription = "Qrcode" )

                }
            }

        }
        }

}





private fun getQrCodeBitmap(uid: String): Bitmap {
    val size = 512 //pixels
    val qrCodeContent = uid
    val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
    val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
        for (x in 0 until size) {
            for (y in 0 until size) {
                it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    }
}


@Composable
fun MainScreenContent(
    dogsState: DataState<ItemDataSummary>,
    onRefresh: () -> Unit = {},
    onSuccess: (ItemDataSummary) -> Unit = {},
    onError: (String) -> Unit = {},
    onFavorite: (Breed) -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = dogsState.loading),
            onRefresh = onRefresh
        ) {
            if (dogsState.empty) {
                Empty()
            }
            val data = dogsState.data
            if (data != null) {
                LaunchedEffect(data) {
                    onSuccess(data)
                }
                Success(successData = data, favoriteBreed = onFavorite)
            }
            val exception = dogsState.exception
            if (exception != null) {
                LaunchedEffect(exception) {
                    onError(exception)
                }
                Error(exception)
            }
        }
    }
}


@Composable
fun WorkerScreenContent(
    workersState : DataState<List<Worker>>,
    onRefresh: () -> Unit = {},
    onConfirm : (String) -> Unit,

    ){

    val showDialog = remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ){

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = workersState.loading),
            onRefresh = onRefresh
        ){
            Scaffold(
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    FloatingActionButton(onClick = { showDialog.value = true }) {
                        Icon(Icons.Filled.Add,"" )
                    }
                }

            ) {

                if(showDialog.value){
                    AlertDialog(onDismissRequest = { showDialog.value = false},
                        title = {
                            Text(text = stringResource(id = R.string.create_worker_title))
                        },
                        text = {TextField(value = name , onValueChange = {name = it})},
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

                if(workersState.empty){
                    Empty()
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

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = error)
    }
}

@Composable
fun LoginFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: (String,String) -> Unit,
    onSignUpClick : (String,String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please login")

        OutlinedTextField(
            value = email,
            placeholder = { Text(text = "user@email.com") },
            label = { Text(text = "email") },
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        OutlinedTextField(
            value = password,
            placeholder = { Text(text = "password") },
            label = { Text(text = "password") },
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Button(onClick = {
            if (email.isBlank() == false && password.isBlank() == false) {
                onLoginClick(email,password)
                focusManager.clearFocus()
            } else {
                Toast.makeText(
                    context,
                    "Please enter an email and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text("Login")
        }

        Button(onClick = {
            if (email.isBlank() == false && password.isBlank() == false) {
                onSignUpClick(email,password)
                focusManager.clearFocus()
            } else {
                Toast.makeText(
                    context,
                    "Please enter an email and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text("Register")
        }

    }
}



@Composable
fun Success(
    successData: ItemDataSummary,
    favoriteBreed: (Breed) -> Unit
) {
    DogList(breeds = successData.allItems, favoriteBreed)
}

@Composable
fun DogList(breeds: List<Breed>, onItemClick: (Breed) -> Unit) {
    LazyColumn {
        items(breeds) { breed ->
            DogRow(breed) {
                onItemClick(it)
            }
            Divider()
        }
    }
}

@Composable
fun WorkerList(workers : List<Worker>){
    LazyColumn{
        items(workers){
            WorkerRow(worker = it)
            Divider()
        }

    }
}

@Composable
fun DogRow(breed: Breed, onClick: (Breed) -> Unit) {
    Row(
        Modifier
            .clickable { onClick(breed) }
            .padding(10.dp)
    ) {
        Text(breed.name, Modifier.weight(1F))
        FavoriteIcon(breed)
    }
}

@Composable
fun WorkerRow(worker: Worker){
    Row(
        Modifier.padding(10.dp)
    ){
        Text(text = worker.name, Modifier.weight(1F))
        Spacer(Modifier.weight(1f))
        Text(text = worker.uuid, Modifier.weight(1F))
    }
}

@Composable
fun FavoriteIcon(breed: Breed) {
    Crossfade(
        targetState = breed.favorite == 0L,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    ) { fav ->
        if (fav) {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_border_24px),
                contentDescription = stringResource(R.string.favorite_breed, breed.name)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_24px),
                contentDescription = stringResource(R.string.unfavorite_breed, breed.name)
            )
        }
    }
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    MainScreenContent(
        dogsState = DataState(
            data = ItemDataSummary(
                longestItem = null,
                allItems = listOf(
                    Breed(0, "appenzeller", 0),
                    Breed(1, "australian", 1)
                )
            )
        )
    )
}
