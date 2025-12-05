package com.example.tagline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tagline.data.local.entity.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = SearchHistoryItem.MAX_HISTORY_SIZE): Flow<List<SearchHistoryItem>>
    
    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT :limit")
    fun searchHistory(query: String, limit: Int = 10): Flow<List<SearchHistoryItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchHistoryItem)
    
    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteByQuery(query: String)
    
    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
    
    // Keep only the most recent searches
    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun trimHistory(limit: Int = SearchHistoryItem.MAX_HISTORY_SIZE)
    
    // Check if query already exists and update timestamp
    @Query("UPDATE search_history SET timestamp = :timestamp WHERE query = :query")
    suspend fun updateTimestamp(query: String, timestamp: Long = System.currentTimeMillis()): Int
}

