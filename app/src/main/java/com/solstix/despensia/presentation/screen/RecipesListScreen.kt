package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solstix.despensia.model.Pasos
import com.squareup.moshi.Json

@Composable
fun RecipesListScreen(
    navController: NavController,
    recipes: List<Recipe>,
    onClick: (Recipe) -> Unit = {}
) {
    LazyColumn {
        itemsIndexed(recipes) { index, recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = {
                    onClick(recipe)
                })
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: (Recipe) -> Unit = {}
) {
    Column (
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Color.Black, shape = RectangleShape)
            .background(Color.LightGray)
            .padding(8.dp)
            .fillMaxSize()
            .clickable {
                onClick(recipe)
            }
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = recipe.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "${recipe.description.take(25)}...",
            fontSize = 24.sp
        )
        Text(text = "Duración: ${recipe.duration}")
        Text(text = "Dificultad: ${recipe.difficulty}")
    }
}

data class Recipe (
    val title: String,
    val description: String,
    val duration: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<Pasos>
)