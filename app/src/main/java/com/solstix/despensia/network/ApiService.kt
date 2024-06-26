package com.solstix.despensia.network

import com.solstix.despensia.model.ImageDto
import com.solstix.despensia.model.RecipesDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("generar")
    suspend fun getRecipes(
        @Body ingredients: IngredientsBody
    ): RecipesDto

    @Multipart
    @POST("imagen")
    suspend fun getIngredientsFromImage(
        @Part image: MultipartBody.Part
    ): ImageDto
}

@JsonClass(generateAdapter = true)
class IngredientsBody(
    @Json(name = "dificultad") //facil, media, dificil
    val dificultad: String,
    @Json(name = "duracion")
    val duracion: String,
    @Json(name = "equipamiento")
    val equipamiento: List<String>,
    @Json(name = "ingredientes")
    val ingredientes: List<String>,
    @Json(name = "intolerancias")
    val intolerancias: List<String>,
    @Json(name = "perfil")
    val perfil: String, //basico, intermedio, avanzado
    @Json(name = "comensales")
    val comensales: String
)
