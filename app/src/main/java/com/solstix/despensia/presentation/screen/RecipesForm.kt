package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.solstix.despensia.util.ApiState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun RecipesFormScreen(navController: NavController, ingredients: List<IngredientItem>, viewModel: HomeViewModel) {

    Box(Modifier.fillMaxSize()) {
        Text(text = "Recipes Form ${ingredients.toString()}")
        Button(onClick = { viewModel.getProductDetails(ingredients) } ) {
            Text(text = "Enviar")
        }
    }

    val textito = viewModel.productDetailsState.value

    when(textito) {
        is ApiState.Success<RecipesDto> ->
            RecipesListScreen(
                navController = navController,
                recipes = textito.data.map(),
                onClick = {
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = moshi.adapter(Recipe::class.java).lenient()
                    val recipeJson = jsonAdapter.toJson(it)
                    navController.navigate("recipes/$recipeJson")
                }
            )
        is ApiState.Error -> {}
        is ApiState.Loading -> {}
    }
}
