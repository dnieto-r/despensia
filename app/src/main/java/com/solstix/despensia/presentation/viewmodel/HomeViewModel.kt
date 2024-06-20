package com.solstix.despensia.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solstix.despensia.model.RecipesDto
import com.solstix.despensia.presentation.screen.IngredientItem
import com.solstix.despensia.repository.Repository
import com.solstix.despensia.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // TODO: Set UI State
    private val _productDetailsState: MutableState<ApiState<RecipesDto>> =
        mutableStateOf(ApiState.Empty)
    val productDetailsState: State<ApiState<RecipesDto>> get() = _productDetailsState

    fun getProductDetails(ingredients: List<IngredientItem>) {
        val ingredientsString = ingredients.map {
            it.name
        }
        _productDetailsState.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _productDetailsState.value = repository.getRecipes(ingredientsString).also {
                _productDetailsState.value = it
            }
        }
    }
}