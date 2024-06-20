package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
) {
    var expanded by remember { mutableStateOf(false) }
    val difficultyList = arrayOf("Baja", "Media", "Alta")
    var selectedDifficulty by remember { mutableStateOf(difficultyList[0]) }
    val isLactosa = remember { mutableStateOf(false) }
    val isMarisco = remember { mutableStateOf(false) }
    val isGluten = remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("60") }
    var people by remember { mutableStateOf("2") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        Text(
            text = "Nivel de dificultad",
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
            text = "Duración",
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
            onClick = { viewModel.getProductDetails(ingredients) }) {
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
            is ApiState.Loading -> {}
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