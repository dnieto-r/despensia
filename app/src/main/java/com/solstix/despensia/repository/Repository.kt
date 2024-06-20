package com.solstix.despensia.repository

import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.util.ApiState

interface Repository {
    suspend fun getRecipes(ingredient: List<String>): ApiState<RecipesDto>
}