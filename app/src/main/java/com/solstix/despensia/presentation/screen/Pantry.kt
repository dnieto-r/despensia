package com.solstix.despensia.presentation.screen

import android.Manifest
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.solstix.despensia.GoogleAsr
import com.solstix.despensia.MainActivity
import com.solstix.despensia.MainListener
import com.solstix.despensia.PermissionsManager
import com.solstix.despensia.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class IngredientItem(val name: String, val icon: Int, val isChecked: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    storedItemList: MutableList<IngredientItem>,
    addIngredient: (IngredientItem) -> Unit,
    removeIngredient: (IngredientItem) -> Unit,
    navController: NavController
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isTextSelected by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expanded, label = "expandableTransition")

    val iconAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "iconAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val iconOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "iconOffset"
    ) { state ->
        if (state) 0.dp else (-40).dp
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ingredientes disponibles:", fontWeight = FontWeight.Bold) }) },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            val (messages, chatBox, chefButton) = createRefs()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(messages) {
                        top.linkTo(parent.top)
                        bottom.linkTo(chatBox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(storedItemList.size) {
                    val checkedState = remember { mutableStateOf(storedItemList[it].isChecked) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.apple),
                            contentDescription = storedItemList[it].name,
                            modifier = Modifier
                                .height(30.dp)
                                .padding(end = 10.dp)
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF919F88))
                        ) {
                            Text(
                                modifier = Modifier.padding(10.dp),
                                text = storedItemList[it].name
                            )
                        }
                        Box(
                            modifier = Modifier.width(40.dp).padding(start = 7.dp).clickable {
                                removeIngredient.invoke(storedItemList[it])
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val listType = Types.newParameterizedType(List::class.java, IngredientItem::class.java)
                    val jsonAdapter = moshi.adapter<List<IngredientItem>>(listType).lenient()

                    val recipeJson = jsonAdapter.toJson(storedItemList)
                    navController.navigate("recipes_form/$recipeJson")
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .constrainAs(chefButton) {
                        bottom.linkTo(parent.bottom, margin = 70.dp)
                        end.linkTo(parent.end)
                    },
                containerColor = Color(0xFFFF7d26)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chef),
                    contentDescription = "Enviar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                Modifier
                    .constrainAs(chatBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                AnimatedVisibility(visible = expanded) {
                    IconButton(
                        onClick = { /* Acción del icono */ },
                        modifier = Modifier
                            .offset(y = iconOffset)
                            .alpha(iconAlpha)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Favorite",
                            tint = Color.Blue
                        )
                    }
                }
                var asr: GoogleAsr? = null
                asr = GoogleAsr(LocalContext.current, object : MainListener {
                    override fun onResult(text: String?) {
                        Log.d("JORGETESTO", "MainActivity onResult: $text")
                        val ingredients = text?.split("y") ?: emptyList()
                        ingredients.forEach {
                            addIngredient.invoke(IngredientItem(it, 0))
                        }
                        asr?.stopAndDestroy()
                    }
                })
                val context = LocalContext.current
                val permission = Manifest.permission.RECORD_AUDIO
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    PermissionsManager.checkPermission(
                        context as MainActivity,
                        isGranted,
                        permission,
                        acceptedAction = {
                            initAsr(asr = asr)
                            Log.d("JORGETESTO", "MainActivity permisssion accepted")
                        }
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    IconButton(
                        onClick = { requestPermission(requestPermissionLauncher) },
                        modifier = Modifier
                            .offset(y = iconOffset)
                            .alpha(iconAlpha)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_mic_24),
                            contentDescription = "Share",
                            tint = Color.Blue
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .height(58.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .weight(1f),
                    ) {
                        if (isTextSelected) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(80.dp)
                                    .clickable(onClick = {
                                        expanded = !expanded
                                    }),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_attach_file_24),
                                    contentDescription = "Expand",
                                    modifier = Modifier
                                )
                            }
                            TextField(
                                value = text,
                                onValueChange = { newText -> text = newText },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 0.dp)
                                    .fillMaxHeight(),
                                placeholder = { Text("Introduce ingredientes") },
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            if (isTextSelected) {
                                if (text.isNotBlank()) {
                                    addIngredient.invoke(IngredientItem(text, 0))
                                    text = ""
                                }
                            } else {
                                isTextSelected = true
                            }
                        },

                        shape = CircleShape,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(56.dp),
                        containerColor = Color.Blue
                    ) {
                        Icon(
                            if (isTextSelected) Icons.AutoMirrored.Rounded.Send else Icons.Default.Add,
                            contentDescription = "Enviar",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun requestPermission(requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
}

fun initAsr(asr: GoogleAsr) {
    asr.startListening()
}
