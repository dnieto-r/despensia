package com.solstix.despensia.repository

import com.solstix.despensia.model.ProductDetailsDto
import com.solstix.despensia.network.ApiService
import com.solstix.despensia.util.ApiState
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {

    override suspend fun getProductDetails(): ApiState<ProductDetailsDto> = try {
        ApiState.Success(apiService.getProductDetails())
    } catch (e: Exception) {
        ApiState.Error(errorMsg = e.message.toString())
    }
}