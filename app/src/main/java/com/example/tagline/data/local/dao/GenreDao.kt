package com.example.tagline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tagline.data.local.entity.CachedGenre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    
    @Query("SELECT * FROM genres")
    fun getAllGenres(): Flow<List<CachedGenre>>
    
    @Query("SELECT * FROM genres WHERE type = :type")
    fun getGenresByType(type: String): Flow<List<CachedGenre>>
    
    @Query("SELECT * FROM genres WHERE id = :id")
    suspend fun getGenreById(id: Int): CachedGenre?
    
    @Query("SELECT name FROM genres WHERE id IN (:ids)")
    suspend fun getGenreNamesByIds(ids: List<Int>): List<String>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(genres: List<CachedGenre>)
    
    @Query("DELETE FROM genres")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM genres")
    suspend fun getCount(): Int
}

