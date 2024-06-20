package com.solstix.despensia.network

import com.solstix.despensia.model.RecipesDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("recetas") // TODO: Set API Endpoint
    suspend fun getRecipes(
        @Body ingredients: IngredientsBody
    ): RecipesDto // TODO: Set API Response
}

@JsonClass(generateAdapter = true)
class IngredientsBody(
    @Json(name = "dificultad")//baja, media, alta
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
    val perfil: String //basico, intermedio, avanzado
)