package com.example.tagline.domain.usecase

import com.example.tagline.domain.repository.SavedMediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for removing a media item from user's list.
 * Encapsulates the business logic for removing saved media.
 */
class RemoveFromListUseCase @Inject constructor(
    private val savedMediaRepository: SavedMediaRepository
) {
    operator fun invoke(itemId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        
        try {
            savedMediaRepository.removeItem(itemId)
            emit(Resource.Success(Unit))
        } catch (e: IOException) {
            emit(Resource.Error("Erro de ligação. Verifique a internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao remover item"))
        }
    }
}

