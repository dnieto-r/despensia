package com.solstix.despensia.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipesDto(
//    @Json(name = "id")
//    val id: Int,
    @Json(name = "dificultad")
    val difficulty: String,
    @Json(name = "duracion")
    val duration: String,
    @Json(name = "titulo")
    val title: String,
    @Json(name = "descripcion")
    val description: String,
    @Json(name = "ingredientes")
    val ingredients: List<String>,
    @Json(name = "procedimiento")
    val steps: List<Pasos>
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    @Json(name = "ingredientes")
    val ingredientes: List<String>,
)


data class Pasos (
    @Json(name = "paso")
    val title: String,
    @Json(name = "instrucciones")
    val instructions: List<String>
)