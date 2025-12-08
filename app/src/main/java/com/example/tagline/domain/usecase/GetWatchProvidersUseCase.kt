package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.CountryWatchProviders
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.repository.MediaRepository
import com.example.tagline.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for getting watch providers for a media item.
 * Encapsulates the business logic for fetching streaming platform information.
 */
class GetWatchProvidersUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(
        mediaId: Int,
        mediaType: MediaType,
        country: String = "PT"
    ): Flow<Resource<CountryWatchProviders?>> = flow {
        emit(Resource.Loading())
        
        try {
            val result = when (mediaType) {
                MediaType.MOVIE -> mediaRepository.getMovieWatchProviders(mediaId, country)
                MediaType.TV -> mediaRepository.getTvWatchProviders(mediaId, country)
            }
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error("Erro de servidor: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Verifique a sua ligação à internet"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Erro ao obter plataformas de streaming"))
        }
    }
}

