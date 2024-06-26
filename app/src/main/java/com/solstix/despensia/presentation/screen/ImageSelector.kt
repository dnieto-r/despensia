package com.solstix.despensia.presentation.screen

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.solstix.despensia.model.ImageDto
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.solstix.despensia.util.ApiState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageSelectorScreen(navController: NavController,
                        viewModel: HomeViewModel,
                        addIngredients: (ApiState<ImageDto>) -> Unit
                        ) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("ExampleScreen","PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("ExampleScreen","PERMISSION DENIED")
        }
    }

    LaunchedEffect(key1 = viewModel.imageDetailsState.value) {
        if (viewModel.imageDetailsState.value is ApiState.Success) {
            addIngredients(viewModel.imageDetailsState.value)
            viewModel.clearImageDetailsState()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(onClick = {
                Log.d("TEST", "HOLA ${permissionState.hasPermission}")

                if (permissionState.hasPermission) {
                    galleryLauncher.launch("image/*")
                } else {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }) {
                Text(text = "Seleccionar imagen")
            }

            Spacer(modifier = Modifier.height(20.dp))

            imageUri?.let {
                Image(
                    painter = rememberImagePainter(data = it),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }
            if (imageUri != null && viewModel.imageDetailsState.value !is ApiState.Loading) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7d26),
                        contentColor = Color.White
                    ),
                    onClick = {
                        imageUri?.let {
                            val file = createTempFile()
                            it.let { context.contentResolver.openInputStream(it) }.use { input ->
                                file.outputStream().use { output ->
                                    input?.copyTo(output)
                                }
                            }
                            viewModel.getIngredients(file) {
                                viewModel.clearImageDetailsState()
                                file.delete()
                            }
                        }
                    }) {
                    Text(text = "Identificar ingredientes")
                }
            }
            if (viewModel.imageDetailsState.value is ApiState.Loading) {
                LoadingScreen()
            }
        }
    }

}
