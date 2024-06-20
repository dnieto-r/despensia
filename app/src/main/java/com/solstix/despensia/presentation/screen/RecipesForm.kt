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
import com.solstix.despensia.presentation.viewmodel.HomeViewModel

@Composable
fun RecipesFormScreen(navController: NavController, ingredients: List<IngredientItem>, viewModel: HomeViewModel) {
    Box(Modifier.fillMaxSize()) {
        Text(text = "Recipes Form ${ingredients.toString()}")
        Button(onClick = { viewModel.getProductDetails(ingredients) } ) {
            Text(text = "Enviar")
        }
    }
}
