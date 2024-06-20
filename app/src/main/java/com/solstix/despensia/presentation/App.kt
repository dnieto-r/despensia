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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solstix.despensia.presentation.screen.FavoriteScreen
import com.solstix.despensia.presentation.screen.IngredientItem
import com.solstix.despensia.presentation.screen.PantryScreen
import com.solstix.despensia.presentation.screen.Recipe
import com.solstix.despensia.presentation.screen.RecipeDetailScreen
import com.solstix.despensia.presentation.screen.RecipesFormScreen
import com.solstix.despensia.presentation.screen.RecipesListScreen
import com.solstix.despensia.presentation.screen.SettingsScreen
import com.solstix.despensia.presentation.viewmodel.HomeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<HomeViewModel>()
    val itemList = remember { mutableStateListOf<IngredientItem>() }

    fun addIngredient(item: IngredientItem) {
        itemList.add(item)
    }

    fun removeIngredient(item: IngredientItem) {
        itemList.remove(item)
    }

    Column (Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = BottomNavItem.Pantry.route, modifier = Modifier.weight(1f)) {
            composable(BottomNavItem.Pantry.route) {
                PantryScreen(
                    itemList,
                    ::addIngredient,
                    ::removeIngredient,
                    navController
                )
            }
            composable(BottomNavItem.Favorites.route) {
                FavoriteScreen(
                    navController
                )
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    navController
                )
            }
            composable(
                "recipesList/{listRecipes}"
            ) {
                val listRecipesJson = it.arguments?.getString("listRecipes")
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val listType = Types.newParameterizedType(List::class.java, Recipe::class.java)
                val jsonAdapter = moshi.adapter<List<Recipe>>(listType).lenient()
                val listRecipes = listRecipesJson?.let { jsonAdapter.fromJson(it) }

                if (listRecipes != null) {
                    RecipesListScreen(navController = navController, recipes = listRecipes)
                }
            }
            composable(
                "recipes/{recipe}"
            ) { backStackEntry ->
                val recipeObjectJson =  backStackEntry.arguments?.getString("recipe")
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter = moshi.adapter(Recipe::class.java).lenient()
                val recipeObject = recipeObjectJson?.let { jsonAdapter.fromJson(it) }

                if (recipeObject != null) {
                    RecipeDetailScreen(navController = navController, recipe = recipeObject)
                }
            }
            composable(
                "recipes_form/{ingredients}"
            ) { backStackEntry ->
                val recipeObjectJson = backStackEntry.arguments?.getString("ingredients")
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val listType = Types.newParameterizedType(List::class.java, IngredientItem::class.java)
                val jsonAdapter = moshi.adapter<List<IngredientItem>>(listType).lenient()
                val recipeObject = recipeObjectJson?.let { jsonAdapter.fromJson(it) }

                if (recipeObject != null) {
                    RecipesFormScreen(navController = navController, ingredients = recipeObject, viewModel = viewModel)
                }
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
