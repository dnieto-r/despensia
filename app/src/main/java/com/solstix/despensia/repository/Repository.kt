package com.solstix.despensia.repository

import com.solstix.despensia.model.ImageDto
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.util.ApiState
import java.io.File

interface Repository {
    suspend fun getRecipes(ingredient: List<String>,
                           difficulty: String,
                           duration: String,
                           intolerances: List<String>,
                           utensils: List<String>,
                           chefLevel: String,
                           diners: String): ApiState<RecipesDto>

    suspend fun getIngredientsFromImage(imageFile: File) : ApiState<ImageDto>
}