package co.touchlab.kampkit.android.ui.composables

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kampkit.android.ui.composables.qrcode.QrCodeAnalyzer
import co.touchlab.kampkit.android.ui.viewmodel.BadgeViewModel
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.response.Badge
import org.koin.androidx.compose.getViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun QrCodeScannerScreen(){
    val viewModel = getViewModel<BadgeViewModel>()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareBadgeFlow = remember(viewModel.badgeFlow,lifecycleOwner){
        viewModel.badgeFlow.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val badgeState by lifecycleAwareBadgeFlow.collectAsState(initial = viewModel.badgeFlow.value)
    
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    if(hasCamPermission){
        QrCodeScannerContent(lifecycleOwner = lifecycleOwner, context = context, badgeState = badgeState, onBadgeSended = {viewModel.badgeResultViewed()},
        onQrCodeRead = {
                code ->
            viewModel.badge(code)
        })
    }
    else{
        Text(text = "No permission")
    }
}

@Composable
fun QrCodeScannerContent(lifecycleOwner: LifecycleOwner,context : Context,badgeState : DataState<Badge>, onQrCodeRead : (String) -> Unit, onBadgeSended : () -> Unit){


    val previewView = remember { PreviewView(context) }

    var code by remember {
        mutableStateOf("")
    }

    var showBadgeResult by remember {
        mutableStateOf(false)
    }

    var analyzeQrCode by remember {
        mutableStateOf(true)
    }

    if(badgeState.data != null){
        showBadgeResult = true
    }

    if(showBadgeResult){
        AlertDialog(onDismissRequest = { },
        buttons = { Button(onClick = {
         onBadgeSended()
            code = ""
            showBadgeResult = false
        analyzeQrCode = true}) {
            Text(text = "OK")
        } },
        title = { Text(text = "${badgeState.data?.worker_uuid} confirmed")})
    }

    val preview = Preview.Builder().build()
    val selector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(
            Size(
                previewView.width,
                previewView.height
            )
        )
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageAnalysis.setAnalyzer(
        ContextCompat.getMainExecutor(context),
        QrCodeAnalyzer { result ->
            code = result
            onQrCodeRead(code)
            analyzeQrCode = false

        }
    )




    LaunchedEffect(key1 = analyzeQrCode, ){
        val cameraProvider = context.getCameraProvider()
        if(analyzeQrCode){
            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )

                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else{
            cameraProvider.unbindAll()
        }
    }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                previewView
            },
            modifier = Modifier.weight(1f)
        )

        Text(
            text = code,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        )
    }

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}