package com.example.tagline.domain.repository

import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for saved media operations.
 * Defined in domain layer - implemented in data layer.
 */
interface SavedMediaRepository {
    
    fun getSavedItems(): Flow<List<SavedMedia>>
    
    fun getSavedItemsByType(mediaType: MediaType): Flow<List<SavedMedia>>
    
    suspend fun addItem(item: SavedMedia): String
    
    suspend fun removeItem(itemId: String)
    
    suspend fun updateItem(item: SavedMedia)
    
    suspend fun toggleWatched(itemId: String, watched: Boolean)
    
    suspend fun isItemSaved(tmdbId: Int, mediaType: MediaType): Boolean
}

