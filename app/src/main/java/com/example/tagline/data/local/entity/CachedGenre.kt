package com.example.tagline.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class CachedGenre(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String // "movie" or "tv"
)

