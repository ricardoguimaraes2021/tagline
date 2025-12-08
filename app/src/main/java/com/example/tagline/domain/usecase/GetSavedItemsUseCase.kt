package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.repository.SavedMediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Use case for getting saved media items.
 * Encapsulates the business logic for retrieving user's saved list.
 */
class GetSavedItemsUseCase @Inject constructor(
    private val savedMediaRepository: SavedMediaRepository
) {
    operator fun invoke(filterType: MediaType? = null): Flow<Resource<List<SavedMedia>>> {
        val flow = if (filterType != null) {
            savedMediaRepository.getSavedItemsByType(filterType)
        } else {
            savedMediaRepository.getSavedItems()
        }
        
        return flow
            .map<List<SavedMedia>, Resource<List<SavedMedia>>> { items ->
                Resource.Success(items)
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Erro ao carregar lista"))
            }
    }
}

