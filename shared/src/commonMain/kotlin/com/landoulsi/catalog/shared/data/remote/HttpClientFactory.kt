package com.landoulsi.catalog.shared.data.remote

import com.landoulsi.catalog.shared.core.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * The engine is supplied by the platform (OkHttp on Android, Darwin on iOS) so
 * this configuration — JSON, timeouts, base URL — stays in common code.
 */
object HttpClientFactory {

    private const val DEFAULT_TIMEOUT_MILLIS = 20_000L

    private val defaultJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    fun create(
        engineClient: HttpClient,
        host: String,
        logger: Logger,
        json: Json = defaultJson,
        timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
        logLevel: LogLevel = LogLevel.NONE,
    ): HttpClient = engineClient.config {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        install(Logging) {
            level = logLevel
            this.logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    logger.debug(message)
                }
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                this.host = host
            }
        }
    }
}
