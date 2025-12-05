package com.example.tagline.data.repository

import com.example.tagline.data.api.TmdbApiService
import com.example.tagline.data.model.tmdb.Genre
import com.example.tagline.data.model.tmdb.MovieDetailsResponse
import com.example.tagline.data.model.tmdb.SearchResponse
import com.example.tagline.data.model.tmdb.TvDetailsResponse
import com.example.tagline.data.model.tmdb.WatchProvidersResponse
import com.example.tagline.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbRepository @Inject constructor(
    private val tmdbApiService: TmdbApiService
) {
    
    suspend fun searchMulti(query: String, page: Int = 1): Resource<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbApiService.searchMulti(query, page)
                // Filter out person results, keep only movies and tv shows
                val filteredResults = response.results.filter { 
                    it.mediaType == "movie" || it.mediaType == "tv" 
                }
                Resource.Success(response.copy(results = filteredResults))
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao pesquisar")
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int): Resource<MovieDetailsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbApiService.getMovieDetails(movieId)
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter detalhes do filme")
            }
        }
    }

    suspend fun getTvDetails(seriesId: Int): Resource<TvDetailsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbApiService.getTvDetails(seriesId)
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter detalhes da série")
            }
        }
    }

    suspend fun getMovieWatchProviders(movieId: Int, country: String = "PT"): Resource<WatchProvidersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbApiService.getMovieWatchProviders(movieId)
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter plataformas de streaming")
            }
        }
    }

    suspend fun getTvWatchProviders(seriesId: Int, country: String = "PT"): Resource<WatchProvidersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbApiService.getTvWatchProviders(seriesId)
                Resource.Success(response)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter plataformas de streaming")
            }
        }
    }

    suspend fun getAllGenres(): Resource<List<Genre>> {
        return withContext(Dispatchers.IO) {
            try {
                val movieGenres = tmdbApiService.getMovieGenres().genres
                val tvGenres = tmdbApiService.getTvGenres().genres
                // Combine and remove duplicates
                val allGenres = (movieGenres + tvGenres).distinctBy { it.id }
                Resource.Success(allGenres)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter géneros")
            }
        }
    }
}

