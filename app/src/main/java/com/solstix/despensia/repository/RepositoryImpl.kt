package com.solstix.despensia.repository

import android.util.Log
import com.solstix.despensia.model.ImageDto
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.network.ApiService
import com.solstix.despensia.network.IngredientsBody
import com.solstix.despensia.util.ApiState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : Repository {

    override suspend fun getIngredientsFromImage(imageFile: File)
        : ApiState<ImageDto> = try {
        val requestFile: RequestBody =
            imageFile.asRequestBody(MultipartBody.FORM)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile)
        Log.d("RepositoryImpl", "getIngredientsFromImage: ${body.body}")
        ApiState.Success(
                apiService.getIngredientsFromImage(
                        image = body
                ))
        } catch (e: Exception) {
            Log.d("RepositoryImpl", "getIngredientsFromImage: $e")
            ApiState.Error(errorMsg = e.message.toString())
        }

    override suspend fun getRecipes(
        ingredients: List<String>,
        difficulty: String,
        duration: String,
        intolerances: List<String>,
        utensils: List<String>,
        chefLevel: String,
        diners: String,
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