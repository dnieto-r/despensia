package com.solstix.despensia.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solstix.despensia.presentation.screen.ImageSelectorScreen
import com.solstix.despensia.presentation.screen.IngredientItem
import com.solstix.despensia.presentation.screen.PantryScreen
import com.solstix.despensia.presentation.screen.Recipe
import com.solstix.despensia.presentation.screen.RecipeDetailScreen
import com.solstix.despensia.presentation.screen.RecipesFormScreen
import com.solstix.despensia.presentation.screen.RecipesListScreen
import com.solstix.despensia.presentation.screen.SettingsScreen
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.solstix.despensia.util.ApiState

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<HomeViewModel>()
    val itemList = remember { mutableStateListOf<IngredientItem>() }

    var recipes: List<Recipe> = remember { emptyList() }
    var favoriteRecipes: MutableList<Recipe> = remember { mutableListOf() }
    var selectedRecipe: Recipe? = remember { null }

    var chefLevel by remember { mutableStateOf("Basico") }
    val utensils = remember {
        mutableStateListOf<String>(
            "horno",
            "batidora",
            "sarten",
            "olla express",
            "microondas"
        )
    }

    fun setChefLevel(value: String) {
        chefLevel = value
    }

    fun setUtensils(value: List<String>) {
        utensils.clear()
        utensils.addAll(value)
    }

    fun addIngredient(item: IngredientItem) {
        itemList.add(item)
    }

    fun removeIngredient(item: IngredientItem) {
        itemList.remove(item)
    }

    Column(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Pantry.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(BottomNavItem.Pantry.route) {
                PantryScreen(
                    itemList,
                    ::addIngredient,
                    ::removeIngredient,
                    navController
                )
            }
            composable(BottomNavItem.Favorites.route) {
                RecipesListScreen(
                    navController = navController,
                    recipes = favoriteRecipes,
                    favorites = favoriteRecipes,
                    addFavorite = { favoriteRecipes.add(it) },
                    clearRecipes = { recipes = emptyList() },
                    selectRecipe = { selectedRecipe = it })
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    navController,
                    chefLevel,
                    ::setChefLevel,
                    utensils,
                    ::setUtensils,
                )
            }
            composable("image_selector") {
                ImageSelectorScreen(
                    navController,
                    viewModel,
                    addIngredients = { apiState ->
                        if (apiState is ApiState.Success) {
                            apiState.data.ingredientes.forEach {
                                itemList.add(IngredientItem(it, 0, false))
                            }
                        }
                        navController.popBackStack()
                    }
                )
            }
            composable(
                "recipesList"
            ) {
                RecipesListScreen(
                    navController = navController,
                    recipes = recipes,
                    favorites = favoriteRecipes,
                    addFavorite = {
                        if (!favoriteRecipes.contains(it))
                            favoriteRecipes.add(it)
                        else {
                            favoriteRecipes.remove(it)
                        }
                    },
                    clearRecipes = { recipes = emptyList() },
                    selectRecipe = { selectedRecipe = it })
            }
            composable(
                "recipes"
            ) { backStackEntry ->
                if (selectedRecipe != null) {
                    RecipeDetailScreen(
                        navController = navController,
                        recipe = selectedRecipe!!,
                        selectRecipe = { selectedRecipe = null })
                }
            }
            composable(
                "recipes_form"
            ) { backStackEntry ->
                    RecipesFormScreen(
                        navController = navController,
                        ingredients = itemList,
                        viewModel = viewModel,
                        utensils = utensils,
                        chefLevel = chefLevel,
                        setRecipes = { listRecipes -> recipes = listRecipes })
            }
        }
        BottomNavigationBar(Modifier.height(50.dp), navController)
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Pantry : BottomNavItem("pantry", Icons.Default.ShoppingCart, "Despensa")
    data object Favorites : BottomNavItem("favorite", Icons.Default.Favorite, "Favoritos")
    data object Settings : BottomNavItem("settings", Icons.Default.Settings, "Configuración")
}

@Composable
fun BottomNavigationBar(modifier: Modifier, navController: NavController) {
    val items = listOf(
        BottomNavItem.Pantry,
        BottomNavItem.Favorites,
        BottomNavItem.Settings
    )
    var selectedItem: BottomNavItem? = null
    BottomAppBar() {
        NavigationBar(
            modifier = modifier
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = {
                        selectedItem = item
                        navController.navigate(item.route)
                    },
                    icon = { Icon(item.icon, contentDescription = "Home") },
                    label = { Text(item.label) }
                )
            }
        }
    }
}
