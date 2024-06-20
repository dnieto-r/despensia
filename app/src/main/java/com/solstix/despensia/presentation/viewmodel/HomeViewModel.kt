package com.solstix.despensia.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solstix.despensia.model.ProductDetailsDto
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
    private val _productDetailsState: MutableState<ApiState<ProductDetailsDto>> =
        mutableStateOf(ApiState.Loading)
    val productDetailsState: State<ApiState<ProductDetailsDto>> get() = _productDetailsState

    init {
        getProductDetails()
    }

    private fun getProductDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _productDetailsState.value = repository.getProductDetails()
        }
    }
}