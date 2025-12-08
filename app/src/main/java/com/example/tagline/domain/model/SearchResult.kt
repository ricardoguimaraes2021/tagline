package com.example.tagline.domain.model

/**
 * Domain model for paginated search results.
 * Pure Kotlin - no framework dependencies.
 */
data class SearchResult(
    val page: Int,
    val results: List<Media>,
    val totalPages: Int,
    val totalResults: Int
)

