package com.example.tagline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tagline.data.local.entity.CachedMediaDetails

@Dao
interface CachedMediaDao {
    
    @Query("SELECT * FROM cached_media WHERE id = :id AND mediaType = :mediaType")
    suspend fun getMediaDetails(id: Int, mediaType: String): CachedMediaDetails?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: CachedMediaDetails)
    
    @Query("DELETE FROM cached_media WHERE id = :id AND mediaType = :mediaType")
    suspend fun delete(id: Int, mediaType: String)
    
    @Query("DELETE FROM cached_media WHERE cachedAt < :threshold")
    suspend fun deleteExpired(threshold: Long)
    
    @Query("DELETE FROM cached_media")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM cached_media")
    suspend fun getCount(): Int
}

