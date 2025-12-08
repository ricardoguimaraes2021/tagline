package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.repository.SavedMediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for adding a media item to user's list.
 * Encapsulates the business logic for saving media.
 */
class AddToListUseCase @Inject constructor(
    private val savedMediaRepository: SavedMediaRepository
) {
    operator fun invoke(item: SavedMedia): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        
        try {
            // Check if already saved
            val isAlreadySaved = savedMediaRepository.isItemSaved(item.tmdbId, item.type)
            if (isAlreadySaved) {
                emit(Resource.Error("Este item já está na sua lista"))
                return@flow
            }
            
            val itemId = savedMediaRepository.addItem(item)
            emit(Resource.Success(itemId))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de ligação. Verifique a internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao adicionar item"))
        }
    }
}

