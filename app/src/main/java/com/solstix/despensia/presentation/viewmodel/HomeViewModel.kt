package com.solstix.despensia.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solstix.despensia.model.ImageDto
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.presentation.screen.IngredientItem
import com.solstix.despensia.repository.Repository
import com.solstix.despensia.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // TODO: Set UI State
    private val _productDetailsState: MutableState<ApiState<RecipesDto>> =
        mutableStateOf(ApiState.Empty)
    private val _imageDetailsState: MutableState<ApiState<ImageDto>> =
        mutableStateOf(ApiState.Empty)
    val productDetailsState: State<ApiState<RecipesDto>> get() = _productDetailsState
    val imageDetailsState: State<ApiState<ImageDto>> get() = _imageDetailsState

    fun getProductDetails(
        ingredients: List<IngredientItem>,
        difficulty: String,
        intolerances: List<String>,
        utensils: List<String>,
        chefLevel: String,
        duration: String,
        diners: String
    ) {
        val ingredientsString = ingredients.map {
            it.name
        }
        _productDetailsState.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _productDetailsState.value = repository.getRecipes(
                ingredientsString,
                difficulty.lowercase(),
                duration,
                intolerances,
                utensils,
                chefLevel.lowercase(),
                diners
                ).also {
                _productDetailsState.value = it
            }
        }
    }

    fun getIngredients(
        image: File,
        removeImage: () -> Unit
    ) {
        _imageDetailsState.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _imageDetailsState.value = repository.getIngredientsFromImage(
                image
            ).also {
                _imageDetailsState.value = it
                removeImage.invoke()
            }
        }
    }

    fun clearProductDetailsState() {
        _productDetailsState.value = ApiState.Empty
    }

    fun clearImageDetailsState() {
        _imageDetailsState.value = ApiState.Empty
    }
}