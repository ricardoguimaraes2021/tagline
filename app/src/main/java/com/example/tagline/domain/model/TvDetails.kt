package com.example.tagline.domain.model

/**
 * Domain model for detailed TV show information.
 * Pure Kotlin - no framework dependencies.
 */
data class TvDetails(
    val id: Int,
    val name: String,
    val originalName: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val rating: Double,
    val voteCount: Int,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val episodeRunTime: List<Int>?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?
) {
    val year: String?
        get() = firstAirDate?.take(4)
    
    val formattedRuntime: String?
        get() = episodeRunTime?.firstOrNull()?.let { "${it}min/ep" }
}

