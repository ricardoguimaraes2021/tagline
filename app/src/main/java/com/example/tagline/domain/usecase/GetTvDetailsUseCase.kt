package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.TvDetails
import com.example.tagline.domain.repository.MediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for getting TV show details.
 * Encapsulates the business logic for fetching TV series information.
 */
class GetTvDetailsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(seriesId: Int): Flow<Resource<TvDetails>> = flow {
        emit(Resource.Loading())
        
        try {
            val result = mediaRepository.getTvDetails(seriesId)
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error("Erro de servidor: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Verifique a sua ligação à internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao obter detalhes da série"))
        }
    }
}

