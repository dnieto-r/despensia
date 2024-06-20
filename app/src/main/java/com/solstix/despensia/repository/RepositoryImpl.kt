package com.solstix.despensia.repository

import android.util.Log
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.network.ApiService
import com.solstix.despensia.network.IngredientsBody
import com.solstix.despensia.util.ApiState
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {

    override suspend fun getRecipes(ingredients: List<String>): ApiState<RecipesDto> = try {
        ApiState.Success(
            apiService.getRecipes(
                IngredientsBody(
                dificultad = "facil",
                duracion = "30",
                equipamiento = listOf("sarten"),
                ingredientes = ingredients,
                intolerancias = listOf("gluten"),
                perfil = "intermedio"
            )
            ))
    } catch (e: Exception) {
        Log.d("RepositoryImpl", "getRecipes: $e")
        ApiState.Error(errorMsg = e.message.toString())
    }
}