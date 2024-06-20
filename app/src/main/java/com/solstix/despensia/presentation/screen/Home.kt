package com.solstix.despensia.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.solstix.despensia.util.ApiState

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val textito = viewModel.productDetailsState.value

    when(textito) {
        is ApiState.Success<RecipesDto> ->
            RecipesListScreen(
                navController = navController,
                recipes = textito.data.map(),
                onClick = {
                    navController.navigate("recipes/$it")
                }
            )
        is ApiState.Error -> {}
        is ApiState.Loading -> {}
    }
}

fun RecipesDto.map(): List<Recipe> {
    return listOf(
        Recipe(
            title = title,
            description = description,
            duration = duration,
            difficulty = difficulty
        )
    )
}