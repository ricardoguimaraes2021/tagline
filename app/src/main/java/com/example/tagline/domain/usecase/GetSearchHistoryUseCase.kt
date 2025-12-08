package com.example.tagline.domain.usecase

import com.example.tagline.domain.repository.SearchHistoryItem
import com.example.tagline.domain.repository.SearchHistoryRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Use case for getting search history.
 * Encapsulates the business logic for retrieving recent searches.
 */
class GetSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    operator fun invoke(limit: Int = 10): Flow<Resource<List<SearchHistoryItem>>> {
        return searchHistoryRepository.getRecentSearches(limit)
            .map<List<SearchHistoryItem>, Resource<List<SearchHistoryItem>>> { items ->
                Resource.Success(items)
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Erro ao carregar histórico"))
            }
    }
}

