package com.example.tagline.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Domain model for search history item.
 */
data class SearchHistoryItem(
    val query: String,
    val searchedAt: Long
)

/**
 * Repository interface for search history operations.
 * Defined in domain layer - implemented in data layer.
 */
interface SearchHistoryRepository {
    
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistoryItem>>
    
    suspend fun addSearchQuery(query: String)
    
    suspend fun deleteSearchQuery(query: String)
    
    suspend fun clearSearchHistory()
}

