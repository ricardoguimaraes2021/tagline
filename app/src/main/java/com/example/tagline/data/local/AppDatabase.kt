package com.example.tagline.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tagline.data.local.dao.CachedMediaDao
import com.example.tagline.data.local.dao.GenreDao
import com.example.tagline.data.local.dao.SearchHistoryDao
import com.example.tagline.data.local.entity.CachedGenre
import com.example.tagline.data.local.entity.CachedMediaDetails
import com.example.tagline.data.local.entity.SearchHistoryItem

@Database(
    entities = [
        CachedGenre::class,
        CachedMediaDetails::class,
        SearchHistoryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun genreDao(): GenreDao
    abstract fun cachedMediaDao(): CachedMediaDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    
    companion object {
        const val DATABASE_NAME = "tagline_database"
    }
}

