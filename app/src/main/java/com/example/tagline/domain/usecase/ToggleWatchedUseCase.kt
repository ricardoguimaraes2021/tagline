package com.example.tagline.domain.usecase

import com.example.tagline.domain.repository.SavedMediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for toggling watched status of a media item.
 * Encapsulates the business logic for marking media as watched/unwatched.
 */
class ToggleWatchedUseCase @Inject constructor(
    private val savedMediaRepository: SavedMediaRepository
) {
    operator fun invoke(itemId: String, watched: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        
        try {
            savedMediaRepository.toggleWatched(itemId, watched)
            emit(Resource.Success(Unit))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de ligação. Verifique a internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao atualizar item"))
        }
    }
}

