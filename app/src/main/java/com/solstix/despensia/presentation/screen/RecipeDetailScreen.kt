package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solstix.despensia.model.Pasos

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipe: Recipe
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = recipe.title.uppercase(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Divider()
        Text(
            text = recipe.description,
            fontSize = 18.sp
        )
        Divider()
        Text(
            text = "Duración: ${recipe.duration} minutos",
            fontSize = 22.sp
        )
        Text(
            text = "Dificultad: ${recipe.difficulty}",
            fontSize = 22.sp
        )
        Divider()
        Text(
            text = "Ingredientes:".uppercase(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        recipe.ingredients.forEach {
            Text(
                text = it,
                fontSize = 18.sp
            )
        }
        Divider()
        Text(
            text = "Pasos:".uppercase(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom=4.dp)
        )
        recipe.steps.forEach {
            Text(
                text = it.title,
                fontSize = 22.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom=4.dp)
            )
            it.instructions.forEach {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start=4.dp, bottom=8.dp)
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

private val overlayFontColor = Color(0xFF000000)

@Composable
fun Divider(
    thickness: Dp = 1.dp,
) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
        thickness = thickness,
        color = overlayFontColor.copy(alpha = 0.25f)
    )

}