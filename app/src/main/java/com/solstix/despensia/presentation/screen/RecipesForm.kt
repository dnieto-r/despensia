package com.solstix.despensia.presentation.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.solstix.despensia.util.ApiState
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesFormScreen(
    navController: NavController,
    ingredients: List<IngredientItem>,
    viewModel: HomeViewModel,
    utensils: List<String>,
    chefLevel: String
) {
    var expanded by remember { mutableStateOf(false) }
    val difficultyList = arrayOf("Baja", "Media", "Alta")
    var selectedDifficulty by remember { mutableStateOf(difficultyList[0]) }
    val isLactosa = remember { mutableStateOf(false) }
    val isMarisco = remember { mutableStateOf(false) }
    val isGluten = remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        Text(
            text = "Nivel de dificultad máxima",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedDifficulty,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    difficultyList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedDifficulty = item
                                expanded = false
                            }
                        )
                    }
                }
            }


        }
        Text(
            text = "Intolerancias",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Lactosa", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isLactosa.value,
                onCheckedChange = { isLactosa.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Marisco", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isMarisco.value,
                onCheckedChange = { isMarisco.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Gluten", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isGluten.value,
                onCheckedChange = { isGluten.value = it },
            )
        }

        Text(
            text = "Duración máxima",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TextField(
                value = duration,
                onValueChange = { newText -> if (newText.length < 4 ) {
                    duration = newText
                } },
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 0.dp),
                placeholder = { Text("") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Text(
                text = "minutos",
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            text = "Comensales",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TextField(
                value = people,
                onValueChange = { newText ->
                    if (newText.length < 3) {
                        people = newText
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 0.dp),
                placeholder = { Text("") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Text(
                text = "personas",
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp),
            onClick = {
                val intolerances = listOf(
                    if (isLactosa.value) "lactosa" else "",
                    if (isMarisco.value) "marisco" else "",
                    if (isGluten.value) "gluten" else ""
                ).filter {
                    it != ""
                }

                viewModel.getProductDetails(
                    ingredients,
                    selectedDifficulty,
                    intolerances,
                    utensils,
                    chefLevel,
                    duration,
                    people)
            }
        ) {
            Text(text = "Enviar")
        }

        val recipes = viewModel.productDetailsState.value

        when(recipes) {
            is ApiState.Success<RecipesDto> -> {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val listType = Types.newParameterizedType(List::class.java, Recipe::class.java)
                val jsonAdapter = moshi.adapter<List<Recipe>>(listType).lenient()
                val recipesJson = jsonAdapter.toJson(recipes.data.map())
                    navController.navigate("recipesList/$recipesJson"
                )
            }
            is ApiState.Error -> {}
            is ApiState.Loading -> { LoadingScreen() }
            is ApiState.Empty -> {}
        }
    }
}

fun RecipesDto.map(): List<Recipe> {
    return listOf(
        Recipe(
            title = title,
            description = description,
            duration = duration,
            difficulty = difficulty,
            ingredients = ingredients,
            steps = steps
        )
    )
}

@Composable
fun LoadingScreen() {
    Column (
        Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressAnimated()
    }
}

@Composable
private fun CircularProgressAnimated(){
    val progressValue = 0.85f
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation")
    val progressAnimationValue by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = progressValue,
        animationSpec = infiniteRepeatable(animation = tween(1500)),
        label = "loadingAnimation"
    )

    CircularProgressIndicator(
        progress = {
            progressAnimationValue
        },
        modifier = Modifier
            .padding(1.dp)
            .height(48.dp)
            .width(48.dp),
        color = Color.Blue,
        strokeWidth = 5.dp,
    )
}