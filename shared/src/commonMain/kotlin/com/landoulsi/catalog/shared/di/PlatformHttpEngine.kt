package com.landoulsi.catalog.shared.di

import io.ktor.client.HttpClient

/**
 * Wraps the platform's Ktor engine so common code can depend on "an engine"
 * without naming OkHttp or Darwin.
 */
class PlatformHttpEngine(val client: HttpClient)
