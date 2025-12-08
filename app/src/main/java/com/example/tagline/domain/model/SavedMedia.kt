package com.example.tagline.domain.model

/**
 * Domain model for a saved media item in user's list.
 * Pure Kotlin - no framework dependencies.
 */
data class SavedMedia(
    val id: String = "",
    val tmdbId: Int,
    val title: String,
    val type: MediaType,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val rating: Double = 0.0,
    val genres: List<String> = emptyList(),
    val genreIds: List<Int> = emptyList(),
    val overview: String? = null,
    val releaseYear: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val watched: Boolean = false,
    val watchedAt: Long? = null,
    val userNotes: String? = null
)

