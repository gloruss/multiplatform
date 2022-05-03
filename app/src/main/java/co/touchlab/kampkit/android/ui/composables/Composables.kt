package co.touchlab.kampkit.android.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
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
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import co.touchlab.kampkit.android.BuildConfig
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.android.ui.composables.getQrCodeBitmap
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.ItemDataSummary
import co.touchlab.kampkit.response.Worker

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream















@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.empty_results))
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
fun WorkerList(workers : List<Worker>){
    LazyColumn{
        items(workers){
            WorkerRow(worker = it)
            Divider()
        }

    }
}



@Composable
fun WorkerRow(worker: Worker ){
    val bitmap = getQrCodeBitmap(worker.uuid)
    val context = LocalContext.current
    Row(
        Modifier.padding(10.dp)
    ){
        Text(text = worker.name, Modifier.weight(1F))
        Spacer(Modifier.weight(1f))
        Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Qrcode", modifier = Modifier.clickable { shareBitmap(bitmap,
            context) } )

    }
}






fun shareBitmap(bit : Bitmap, context : Context){

    val filesDir: File = context.applicationContext.filesDir
    val imageFile = File(filesDir, "birds.png")
    val os: OutputStream
    try {
        os = FileOutputStream(imageFile)
        bit.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.flush()
        os.close()
    } catch (e: Exception) {

    }

    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

    val imageUri: Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, imageFile)

    intent.putExtra(Intent.EXTRA_STREAM, imageUri)
    intent.type = "image/jpg"
    context.startActivity(Intent.createChooser(intent,""))
}
