package com.example.tagline.data.remote.dto

import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Entity for Firestore document.
 * Contains Firebase-specific annotations.
 */
data class SavedItemEntity(
    @DocumentId
    val id: String = "",
    val tmdbId: Int = 0,
    val title: String = "",
    val type: String = "MOVIE",
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
        "type" to type,
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

// ==================== MAPPERS ====================

fun SavedItemEntity.toSavedMedia(): SavedMedia {
    return SavedMedia(
        id = id,
        tmdbId = tmdbId,
        title = title,
        type = MediaType.valueOf(type),
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = rating,
        genres = genres,
        genreIds = genreIds,
        overview = overview,
        releaseYear = releaseYear,
        addedAt = addedAt.toDate().time,
        watched = watched,
        watchedAt = watchedAt?.toDate()?.time,
        userNotes = userNotes
    )
}

fun SavedMedia.toEntity(): SavedItemEntity {
    return SavedItemEntity(
        id = id,
        tmdbId = tmdbId,
        title = title,
        type = type.name,
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = rating,
        genres = genres,
        genreIds = genreIds,
        overview = overview,
        releaseYear = releaseYear,
        addedAt = Timestamp(java.util.Date(addedAt)),
        watched = watched,
        watchedAt = watchedAt?.let { Timestamp(java.util.Date(it)) },
        userNotes = userNotes
    )
}

