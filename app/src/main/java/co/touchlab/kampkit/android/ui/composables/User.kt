package co.touchlab.kampkit.android.ui.composables

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.android.ui.Error
import co.touchlab.kampkit.android.ui.LoginFields
import co.touchlab.kampkit.android.ui.viewmodel.UserViewModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.response.User
import co.touchlab.kermit.Logger
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter



@Composable
fun LoginScreen(
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


    UserScreenContent(
        userState = userState,
        onLoginClick = { email, password -> userViewModel.login(email, password) })

}

@Composable
fun UserScreenContent(
    userState : DataState<User>,
    onLoginClick: (String, String) -> Unit
){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ){

        Column(modifier = Modifier.fillMaxSize()) {
            if(userState.data == null){
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
                if(userState.exception != null){
                    Error("${userState.exception} ")
                }

            }
            else {


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


}

 fun getQrCodeBitmap(uid: String): Bitmap {
    val size = 256 //pixels
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