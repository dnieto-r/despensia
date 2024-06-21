package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipe: Recipe,
    selectRecipe: () -> Unit
) {

    DisposableEffect(Unit) {
        onDispose {
            selectRecipe.invoke()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe.imagen).build(),
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 5.dp),
            contentDescription = "comida",
        )
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
        if (recipe.duration != "") {
            Text(
                text = "Duración: ${recipe.duration} minutos",
                fontSize = 22.sp
            )
        }
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