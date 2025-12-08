package com.example.tagline.domain.repository

import com.example.tagline.domain.model.CountryWatchProviders
import com.example.tagline.domain.model.Genre
import com.example.tagline.domain.model.MovieDetails
import com.example.tagline.domain.model.SearchResult
import com.example.tagline.domain.model.TvDetails

/**
 * Repository interface for media operations.
 * Defined in domain layer - implemented in data layer.
 */
interface MediaRepository {
    
    suspend fun searchMulti(query: String, page: Int = 1): SearchResult
    
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    
    suspend fun getTvDetails(seriesId: Int): TvDetails
    
    suspend fun getMovieWatchProviders(movieId: Int, country: String = "PT"): CountryWatchProviders?
    
    suspend fun getTvWatchProviders(seriesId: Int, country: String = "PT"): CountryWatchProviders?
    
    suspend fun getAllGenres(): List<Genre>
    
    suspend fun getGenreNamesByIds(ids: List<Int>): List<String>
}

