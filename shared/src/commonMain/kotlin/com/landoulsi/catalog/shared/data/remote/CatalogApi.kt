package com.landoulsi.catalog.shared.data.remote

import com.landoulsi.catalog.shared.data.remote.dto.ProductDto
import com.landoulsi.catalog.shared.data.remote.dto.ProductListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Returns DTOs and lets exceptions propagate; translating both into domain types
 * is the repository's responsibility.
 */
class CatalogApi(
    private val httpClient: HttpClient,
) {

    suspend fun getProducts(skip: Int, limit: Int): ProductListResponseDto =
        httpClient.get(PRODUCTS_PATH) {
            parameter(LIMIT_PARAM, limit)
            parameter(SKIP_PARAM, skip)
        }.body()

    suspend fun searchProducts(query: String, skip: Int, limit: Int): ProductListResponseDto =
        httpClient.get("$PRODUCTS_PATH$SEARCH_PATH") {
            parameter(QUERY_PARAM, query)
            parameter(LIMIT_PARAM, limit)
            parameter(SKIP_PARAM, skip)
        }.body()

    suspend fun getProduct(id: Int): ProductDto = httpClient.get("$PRODUCTS_PATH/$id").body()

    companion object {
        const val HOST = "dummyjson.com"

        private const val PRODUCTS_PATH = "/products"
        private const val SEARCH_PATH = "/search"
        private const val QUERY_PARAM = "q"
        private const val LIMIT_PARAM = "limit"
        private const val SKIP_PARAM = "skip"
    }
}
