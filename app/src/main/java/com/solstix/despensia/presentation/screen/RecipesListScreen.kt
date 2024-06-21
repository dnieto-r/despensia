package com.solstix.despensia.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.solstix.despensia.model.Pasos
import java.util.Locale

@Composable
fun RecipesListScreen(
    navController: NavController,
    recipes: List<Recipe>,
    favorites: List<Recipe>,
    clearRecipes: () -> Unit,
    addFavorite: (Recipe) -> Unit,
    selectRecipe: (Recipe) -> Unit
) {
    BackHandler {
        clearRecipes.invoke()
        navController.popBackStack()
    }

    LazyColumn {
        itemsIndexed(recipes) { index, recipe ->
            RecipeCard(
                recipe = recipe,
                favorites = favorites,
                addFavorite = addFavorite,
                onClick = {
                    selectRecipe(recipe)
                    navController.navigate("recipes")
                })
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    favorites: List<Recipe>,
    onClick: (Recipe) -> Unit = {},
    addFavorite: (Recipe) -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(20.dp))
                .fillMaxSize()
                .clickable {
                    onClick(recipe)
                }
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
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .padding(horizontal = 8.dp),
                text = recipe.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp),
                text = "${recipe.description.take(25)}...",
                fontSize = 24.sp
            )
            if (recipe.duration != "") {
                Text(
                    text = "Duración: ${recipe.duration} minutos",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Text(
                text = "Dificultad: ${recipe.difficulty.capitalize(Locale.US)}",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(24.dp)
                .size(48.dp)
                .align(Alignment.TopEnd)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
        ) {
            IconButton(
                onClick = {
                    isLiked = !isLiked
                    addFavorite(recipe)
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (favorites.contains(recipe) || isLiked) Icons.Default.Favorite else Icons.Outlined.Favorite,
                    contentDescription = "Favorite",
                    tint = if (favorites.contains(recipe) || isLiked) Color.Red else Color.Black
                )
            }
        }
    }
}

data class Recipe (
    val title: String,
    val description: String,
    val duration: String,
    val difficulty: String,
    val ingredients: List<String>,
    val steps: List<Pasos>,
    val imagen: String
)