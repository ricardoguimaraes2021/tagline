package com.example.tagline.domain.usecase

import com.example.tagline.domain.repository.SearchHistoryRepository
import javax.inject.Inject

/**
 * Use case for deleting search history entries.
 * Encapsulates the business logic for managing search history.
 */
class DeleteSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend fun deleteQuery(query: String) {
        searchHistoryRepository.deleteSearchQuery(query)
    }
    
    suspend fun clearAll() {
        searchHistoryRepository.clearSearchHistory()
    }
}

