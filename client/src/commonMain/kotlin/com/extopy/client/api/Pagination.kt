package com.extopy.client.api

import io.ktor.client.request.*

/**
 * Adds the window a listing endpoint reads, leaving out whatever the caller did not ask for so the
 * server falls back to its own defaults.
 */
internal fun HttpRequestBuilder.paginate(limit: Long?, offset: Long?, search: String? = null) {
    limit?.let { parameter("limit", it) }
    offset?.let { parameter("offset", it) }
    search?.let { parameter("search", it) }
}
