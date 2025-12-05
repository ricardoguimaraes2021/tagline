package com.example.tagline.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_media")
data class CachedMediaDetails(
    @PrimaryKey
    val id: Int,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: Int?, // in minutes
    val genres: String, // JSON string of genre names
    val genreIds: String, // JSON string of genre IDs
    val tagline: String?,
    val status: String?,
    val numberOfSeasons: Int?, // for TV shows
    val numberOfEpisodes: Int?, // for TV shows
    val cachedAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Cache validity: 24 hours
        const val CACHE_VALIDITY_MS = 24 * 60 * 60 * 1000L
    }
    
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - cachedAt > CACHE_VALIDITY_MS
    }
}

