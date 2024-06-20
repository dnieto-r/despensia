package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solstix.despensia.model.Pasos

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipe: Recipe
) {
    Column {
        Text(
            text = recipe.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = recipe.description,
            fontSize = 18.sp
        )
        Text(
            text = "Duración: ${recipe.duration}",
            fontSize = 18.sp
        )
        Text(
            text = "Dificultad: ${recipe.difficulty}",
            fontSize = 18.sp
        )
        Text(
            text = "Ingredientes:",
            fontSize = 18.sp
        )
        recipe.ingredients.forEach {
            Text(
                text = it,
                fontSize = 18.sp
            )
        }
        Text(
            text = "Pasos:",
            fontSize = 18.sp
        )
        recipe.steps.forEach {
            Text(
                text = it.title,
                fontSize = 18.sp
            )
            it.instructions.forEach {
                Text(
                    text = it,
                    fontSize = 18.sp
                )
            }
        }
    }
}

//preview
@Composable
fun RecipeDetailScreenPreview() {
    RecipeDetailScreen(
        navController = NavController(LocalContext.current),
        recipe = Recipe(
            title = "Huevos roto con jamón",
            description = "Huevos rotos con jamón",
            duration = "10 minutos",
            difficulty = "Fácil",
            ingredients = listOf("Huevos", "Patatas", "Jamón"),
            steps = listOf(
                Pasos(
                    title = "Pelar patatas",
                    instructions = listOf("Pelar las patatas", "Trocear en pajitas", "Lavar las patatas")
                ),
                Pasos(
                    title = "Freir patatas",
                    instructions = listOf("Calentar aceite", "Freir las patatas durante 20 minutos")
                ),
                Pasos(
                    title = "Freir huevos",
                    instructions = listOf("Calentar aceite", "Freir huevos")
                ),
                Pasos(
                    title = "Cortar jamón",
                    instructions = listOf("Cortar el jamón en trozos")
                ),
                Pasos(
                    title = "Emplatar",
                    instructions = listOf("Juntar todo en un plato")
                )
            )
        )
    )
}