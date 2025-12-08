package com.example.tagline.domain.model

/**
 * Domain model representing a media item (movie or TV show).
 * Pure Kotlin - no framework dependencies.
 */
data class Media(
    val id: Int,
    val title: String,
    val originalTitle: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val mediaType: MediaType,
    val genreIds: List<Int> = emptyList()
) {
    val year: String?
        get() = releaseDate?.take(4)
}

enum class MediaType {
    MOVIE,
    TV;

    companion object {
        fun fromString(value: String): MediaType {
            return when (value.lowercase()) {
                "movie" -> MOVIE
                "tv", "tv_series" -> TV
                else -> MOVIE
            }
        }
    }
}

