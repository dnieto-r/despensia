package com.solstix.despensia.repository

import com.solstix.despensia.model.ProductDetailsDto
import com.solstix.despensia.util.ApiState

interface Repository {
    suspend fun getProductDetails(): ApiState<ProductDetailsDto>
}