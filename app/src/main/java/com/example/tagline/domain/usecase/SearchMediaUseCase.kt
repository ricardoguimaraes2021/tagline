package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.SearchResult
import com.example.tagline.domain.repository.MediaRepository
import com.example.tagline.domain.repository.SearchHistoryRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for searching media (movies and TV shows).
 * Encapsulates the business logic for search operations.
 */
class SearchMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) {
    operator fun invoke(query: String, page: Int = 1): Flow<Resource<SearchResult>> = flow {
        if (query.isBlank()) {
            emit(Resource.Error("A pesquisa não pode estar vazia"))
            return@flow
        }
        
        emit(Resource.Loading())
        
        try {
            // Save search query to history
            if (query.isNotBlank()) {
                searchHistoryRepository.addSearchQuery(query)
            }
            
            val result = mediaRepository.searchMulti(query, page)
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error("Erro de servidor: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Verifique a sua ligação à internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro desconhecido ao pesquisar"))
        }
    }
}

