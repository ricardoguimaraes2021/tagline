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
    private val tmdbApiService: TmdbApiService,
    private val localCacheRepository: LocalCacheRepository
) {
    
    suspend fun searchMulti(query: String, page: Int = 1): Resource<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Save search query to history
                if (query.isNotBlank()) {
                    localCacheRepository.addSearchQuery(query)
                }
                
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
                // Check cache first
                val cached = localCacheRepository.getCachedMovieDetails(movieId)
                if (cached != null) {
                    return@withContext Resource.Success(localCacheRepository.cachedToMovieDetails(cached))
                }
                
                // Fetch from API
                val response = tmdbApiService.getMovieDetails(movieId)
                
                // Cache the response
                localCacheRepository.cacheMovieDetails(response)
                
                Resource.Success(response)
            } catch (e: Exception) {
                // Try to return cached data even if expired
                val cached = localCacheRepository.getCachedMovieDetails(movieId)
                if (cached != null) {
                    Resource.Success(localCacheRepository.cachedToMovieDetails(cached))
                } else {
                    Resource.Error(e.message ?: "Erro ao obter detalhes do filme")
                }
            }
        }
    }

    suspend fun getTvDetails(seriesId: Int): Resource<TvDetailsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Check cache first
                val cached = localCacheRepository.getCachedTvDetails(seriesId)
                if (cached != null) {
                    return@withContext Resource.Success(localCacheRepository.cachedToTvDetails(cached))
                }
                
                // Fetch from API
                val response = tmdbApiService.getTvDetails(seriesId)
                
                // Cache the response
                localCacheRepository.cacheTvDetails(response)
                
                Resource.Success(response)
            } catch (e: Exception) {
                // Try to return cached data even if expired
                val cached = localCacheRepository.getCachedTvDetails(seriesId)
                if (cached != null) {
                    Resource.Success(localCacheRepository.cachedToTvDetails(cached))
                } else {
                    Resource.Error(e.message ?: "Erro ao obter detalhes da série")
                }
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
                // Check if genres are cached
                if (localCacheRepository.hasGenresCached()) {
                    // Return cached genres (they don't change often)
                    val cachedGenres = mutableListOf<Genre>()
                    localCacheRepository.getAllGenres().collect { genres ->
                        cachedGenres.addAll(genres)
                    }
                    if (cachedGenres.isNotEmpty()) {
                        return@withContext Resource.Success(cachedGenres.distinctBy { it.id })
                    }
                }
                
                // Fetch from API
                val movieGenres = tmdbApiService.getMovieGenres().genres
                val tvGenres = tmdbApiService.getTvGenres().genres
                
                // Cache genres
                localCacheRepository.cacheGenres(movieGenres, "movie")
                localCacheRepository.cacheGenres(tvGenres, "tv")
                
                // Combine and remove duplicates
                val allGenres = (movieGenres + tvGenres).distinctBy { it.id }
                Resource.Success(allGenres)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Erro ao obter géneros")
            }
        }
    }
    
    // Get genre names for a list of IDs (using cache)
    suspend fun getGenreNamesByIds(ids: List<Int>): List<String> {
        return localCacheRepository.getGenreNamesByIds(ids)
    }
    
    // Clear expired cache
    suspend fun clearExpiredCache() {
        localCacheRepository.clearExpiredCache()
    }
}
