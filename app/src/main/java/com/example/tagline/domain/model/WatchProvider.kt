package com.example.tagline.domain.model

/**
 * Domain model for watch provider information.
 * Pure Kotlin - no framework dependencies.
 */
data class WatchProvider(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val displayPriority: Int
)

/**
 * Domain model for country-specific watch providers.
 */
data class CountryWatchProviders(
    val link: String?,
    val flatrate: List<WatchProvider> = emptyList(),
    val rent: List<WatchProvider> = emptyList(),
    val buy: List<WatchProvider> = emptyList(),
    val free: List<WatchProvider> = emptyList()
)

