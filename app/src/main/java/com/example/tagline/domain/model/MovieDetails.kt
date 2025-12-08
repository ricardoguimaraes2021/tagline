package com.example.tagline.domain.model

/**
 * Domain model for detailed movie information.
 * Pure Kotlin - no framework dependencies.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double,
    val voteCount: Int,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?,
    val imdbId: String?
) {
    val year: String?
        get() = releaseDate?.take(4)
    
    val formattedRuntime: String?
        get() = runtime?.let { "${it}min" }
}

