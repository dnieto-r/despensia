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

    override suspend fun getRecipes(
        ingredients: List<String>,
        difficulty: String,
        duration: String,
        intolerances: List<String>,
        utensils: List<String>,
        chefLevel: String,
        diners: String
    ): ApiState<RecipesDto> = try {
        ApiState.Success(
            apiService.getRecipes(
                IngredientsBody(
                    dificultad = difficulty,
                    duracion = duration,
                    equipamiento = utensils,
                    ingredientes = ingredients,
                    intolerancias = intolerances,
                    perfil = chefLevel,
                    comensales = diners
            )
            ))
    } catch (e: Exception) {
        Log.d("RepositoryImpl", "getRecipes: $e")
        ApiState.Error(errorMsg = e.message.toString())
    }
}