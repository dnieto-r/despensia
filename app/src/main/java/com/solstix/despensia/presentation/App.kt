package com.solstix.despensia.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solstix.despensia.presentation.screen.HomeScreen
import com.solstix.despensia.presentation.viewmodel.HomeViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<HomeViewModel>()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, viewModel)
        }
    }
}