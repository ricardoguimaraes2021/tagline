package com.example.tagline.data.repository

import com.example.tagline.data.local.dao.SearchHistoryDao
import com.example.tagline.data.local.entity.SearchHistoryItem as SearchHistoryEntity
import com.example.tagline.domain.repository.SearchHistoryItem
import com.example.tagline.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getRecentSearches(limit: Int): Flow<List<SearchHistoryItem>> {
        return searchHistoryDao.getRecentSearches().map { entities ->
            entities.take(limit).map { entity ->
                SearchHistoryItem(
                    query = entity.query,
                    searchedAt = entity.timestamp
                )
            }
        }
    }

    override suspend fun addSearchQuery(query: String) {
        // First try to update existing query's timestamp
        val updated = searchHistoryDao.updateTimestamp(query)
        if (updated == 0) {
            // Query doesn't exist, insert new one
            searchHistoryDao.insert(SearchHistoryEntity(query = query))
        }
        // Trim old entries
        searchHistoryDao.trimHistory()
    }

    override suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteByQuery(query)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.deleteAll()
    }
}

