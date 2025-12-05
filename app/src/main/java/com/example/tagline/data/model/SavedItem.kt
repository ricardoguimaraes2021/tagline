package com.example.tagline.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class SavedItem(
    @DocumentId
    val id: String = "",
    val tmdbId: Int = 0,
    val title: String = "",
    val type: MediaType = MediaType.MOVIE,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val rating: Double = 0.0,
    val genres: List<String> = emptyList(),
    val genreIds: List<Int> = emptyList(),
    val overview: String? = null,
    val releaseYear: String? = null,
    val addedAt: Timestamp = Timestamp.now(),
    val watched: Boolean = false,
    val watchedAt: Timestamp? = null,
    val userNotes: String? = null
) {
    // No-arg constructor for Firestore
    constructor() : this(id = "")
    
    fun toMap(): Map<String, Any?> = mapOf(
        "tmdbId" to tmdbId,
        "title" to title,
        "type" to type.name,
        "posterPath" to posterPath,
        "backdropPath" to backdropPath,
        "rating" to rating,
        "genres" to genres,
        "genreIds" to genreIds,
        "overview" to overview,
        "releaseYear" to releaseYear,
        "addedAt" to addedAt,
        "watched" to watched,
        "watchedAt" to watchedAt,
        "userNotes" to userNotes
    )
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

