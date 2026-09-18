package com.landoulsi.catalog.shared.data

import com.landoulsi.catalog.shared.core.CatalogError
import com.landoulsi.catalog.shared.core.DataResult
import com.landoulsi.catalog.shared.data.mapper.ProductMapperImpl
import com.landoulsi.catalog.shared.data.remote.CatalogApi
import com.landoulsi.catalog.shared.data.remote.HttpClientFactory
import com.landoulsi.catalog.shared.data.repository.ProductRepositoryImpl
import com.landoulsi.catalog.shared.domain.model.ProductPage
import com.landoulsi.catalog.shared.fake.FakeLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProductRepositoryImplTest {

    private val listBody = """
        {"products":[{"id":1,"title":"Phone","description":"A phone","price":100.0,"discountPercentage":0.0,"rating":4.0,"stock":10,"brand":"Brand","category":"phones","thumbnail":"thumb","images":["image"]}],"total":194,"skip":0,"limit":20}
    """.trimIndent()

    /** Builds a repository whose HTTP calls are served by [handler]. */
    private fun repository(
        handler: MockRequestHandleScope.() -> HttpResponseData,
    ): Pair<ProductRepositoryImpl, MutableList<String>> {
        val requestedUrls = mutableListOf<String>()
        val engine = MockEngine { request ->
            requestedUrls += request.url.toString()
            handler()
        }
        val client = HttpClientFactory.create(HttpClient(engine), host = CatalogApi.HOST, logger = FakeLogger())
        val repository = ProductRepositoryImpl(CatalogApi(client), ProductMapperImpl(), FakeLogger())
        return repository to requestedUrls
    }

    private fun MockRequestHandleScope.json(body: String) = respond(
        content = body,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

    @Test
    fun `uses the list endpoint when no query is given`() = runTest {
        val (repository, urls) = repository { json(listBody) }

        val result = repository.getProducts(query = "", skip = 0, limit = 20)

        assertIs<DataResult.Success<*>>(result)
        val url = urls.single()
        assertEquals(true, url.contains("/products?"), "expected list endpoint, was $url")
        assertEquals(true, url.contains("limit=20"))
        assertEquals(true, url.contains("skip=0"))
    }

    @Test
    fun `uses the search endpoint when a query is given`() = runTest {
        val (repository, urls) = repository { json(listBody) }

        repository.getProducts(query = "phone", skip = 40, limit = 20)

        val url = urls.single()
        assertEquals(true, url.contains("/products/search"), "expected search endpoint, was $url")
        assertEquals(true, url.contains("q=phone"))
        assertEquals(true, url.contains("skip=40"))
    }

    @Test
    fun `maps a successful response into a domain page`() = runTest {
        val (repository, _) = repository { json(listBody) }

        val result = repository.getProducts(query = "", skip = 0, limit = 20)

        val page = assertIs<DataResult.Success<*>>(result).data as ProductPage
        assertEquals(listOf(1), page.products.map { it.id })
        assertEquals(194, page.total)
        assertEquals(true, page.hasMore)
    }

    @Test
    fun `converts an http error status into a server failure`() = runTest {
        val (repository, _) = repository { respondError(HttpStatusCode.InternalServerError) }

        val result = repository.getProducts(query = "", skip = 0, limit = 20)

        val failure = assertIs<DataResult.Failure>(result)
        assertEquals(CatalogError.Server(code = 500), failure.error)
    }

    @Test
    fun `converts a malformed body into a serialization failure`() = runTest {
        val (repository, _) = repository { json("{ not json at all") }

        val result = repository.getProduct(id = 1)

        val failure = assertIs<DataResult.Failure>(result)
        assertIs<CatalogError.Serialization>(failure.error)
    }
}
