package com.example.tagline.data.repository

import com.example.tagline.data.local.dao.CachedMediaDao
import com.example.tagline.data.local.dao.GenreDao
import com.example.tagline.data.local.entity.CachedGenre
import com.example.tagline.data.local.entity.CachedMediaDetails
import com.example.tagline.data.remote.api.TmdbApiService
import com.example.tagline.data.remote.dto.MovieDetailsDto
import com.example.tagline.data.remote.dto.TvDetailsDto
import com.example.tagline.data.remote.dto.toCountryWatchProviders
import com.example.tagline.data.remote.dto.toGenre
import com.example.tagline.data.remote.dto.toMovieDetails
import com.example.tagline.data.remote.dto.toSearchResult
import com.example.tagline.data.remote.dto.toTvDetails
import com.example.tagline.domain.model.CountryWatchProviders
import com.example.tagline.domain.model.Genre
import com.example.tagline.domain.model.MovieDetails
import com.example.tagline.domain.model.SearchResult
import com.example.tagline.domain.model.TvDetails
import com.example.tagline.domain.repository.MediaRepository
import com.example.tagline.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService,
    private val genreDao: GenreDao,
    private val cachedMediaDao: CachedMediaDao
) : MediaRepository {
    
    private val gson = Gson()

    override suspend fun searchMulti(query: String, page: Int): SearchResult {
        return withContext(Dispatchers.IO) {
            val response = tmdbApiService.searchMulti(query, page)
            response.toSearchResult()
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return withContext(Dispatchers.IO) {
            // Check cache first
            val cached = cachedMediaDao.getMediaDetails(movieId, "movie")
            if (cached != null && !cached.isExpired()) {
                return@withContext cachedToMovieDetails(cached)
            }
            
            // Fetch from API
            val response = tmdbApiService.getMovieDetails(movieId)
            
            // Cache the response
            cacheMovieDetails(response)
            
            response.toMovieDetails()
        }
    }

    override suspend fun getTvDetails(seriesId: Int): TvDetails {
        return withContext(Dispatchers.IO) {
            // Check cache first
            val cached = cachedMediaDao.getMediaDetails(seriesId, "tv")
            if (cached != null && !cached.isExpired()) {
                return@withContext cachedToTvDetails(cached)
            }
            
            // Fetch from API
            val response = tmdbApiService.getTvDetails(seriesId)
            
            // Cache the response
            cacheTvDetails(response)
            
            response.toTvDetails()
        }
    }

    override suspend fun getMovieWatchProviders(movieId: Int, country: String): CountryWatchProviders? {
        return withContext(Dispatchers.IO) {
            val response = tmdbApiService.getMovieWatchProviders(movieId)
            response.results?.get(country)?.toCountryWatchProviders()
        }
    }

    override suspend fun getTvWatchProviders(seriesId: Int, country: String): CountryWatchProviders? {
        return withContext(Dispatchers.IO) {
            val response = tmdbApiService.getTvWatchProviders(seriesId)
            response.results?.get(country)?.toCountryWatchProviders()
        }
    }

    override suspend fun getAllGenres(): List<Genre> {
        return withContext(Dispatchers.IO) {
            // Check if genres are cached
            if (genreDao.getCount() > 0) {
                val cachedGenres = genreDao.getAllGenres().first()
                if (cachedGenres.isNotEmpty()) {
                    return@withContext cachedGenres.map { Genre(it.id, it.name) }.distinctBy { it.id }
                }
            }
            
            // Fetch from API
            val movieGenres = tmdbApiService.getMovieGenres().genres
            val tvGenres = tmdbApiService.getTvGenres().genres
            
            // Cache genres
            genreDao.insertAll(movieGenres.map { CachedGenre(it.id, it.name, "movie") })
            genreDao.insertAll(tvGenres.map { CachedGenre(it.id, it.name, "tv") })
            
            // Combine and remove duplicates
            (movieGenres + tvGenres).map { it.toGenre() }.distinctBy { it.id }
        }
    }

    override suspend fun getGenreNamesByIds(ids: List<Int>): List<String> {
        return withContext(Dispatchers.IO) {
            genreDao.getGenreNamesByIds(ids)
        }
    }
    
    // ==================== CACHE HELPERS ====================
    
    private suspend fun cacheMovieDetails(movie: MovieDetailsDto) {
        val cached = CachedMediaDetails(
            id = movie.id,
            mediaType = "movie",
            title = movie.title,
            originalTitle = movie.originalTitle,
            overview = movie.overview,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            releaseDate = movie.releaseDate,
            voteAverage = movie.voteAverage,
            voteCount = movie.voteCount,
            runtime = movie.runtime,
            genres = gson.toJson(movie.genres.map { it.name }),
            genreIds = gson.toJson(movie.genres.map { it.id }),
            tagline = movie.tagline,
            status = movie.status,
            numberOfSeasons = null,
            numberOfEpisodes = null
        )
        cachedMediaDao.insert(cached)
    }
    
    private suspend fun cacheTvDetails(tv: TvDetailsDto) {
        val cached = CachedMediaDetails(
            id = tv.id,
            mediaType = "tv",
            title = tv.name,
            originalTitle = tv.originalName,
            overview = tv.overview,
            posterPath = tv.posterPath,
            backdropPath = tv.backdropPath,
            releaseDate = tv.firstAirDate,
            voteAverage = tv.voteAverage,
            voteCount = tv.voteCount,
            runtime = tv.episodeRunTime?.firstOrNull(),
            genres = gson.toJson(tv.genres.map { it.name }),
            genreIds = gson.toJson(tv.genres.map { it.id }),
            tagline = tv.tagline,
            status = tv.status,
            numberOfSeasons = tv.numberOfSeasons,
            numberOfEpisodes = tv.numberOfEpisodes
        )
        cachedMediaDao.insert(cached)
    }
    
    private fun cachedToMovieDetails(cached: CachedMediaDetails): MovieDetails {
        val genreNames: List<String> = gson.fromJson(cached.genres, object : TypeToken<List<String>>() {}.type)
        val genreIds: List<Int> = gson.fromJson(cached.genreIds, object : TypeToken<List<Int>>() {}.type)
        val genres = genreNames.zip(genreIds).map { Genre(it.second, it.first) }
        
        return MovieDetails(
            id = cached.id,
            title = cached.title,
            originalTitle = cached.originalTitle ?: cached.title,
            overview = cached.overview,
            posterPath = cached.posterPath,
            backdropPath = cached.backdropPath,
            releaseDate = cached.releaseDate,
            rating = cached.voteAverage,
            voteCount = cached.voteCount,
            runtime = cached.runtime,
            genres = genres,
            tagline = cached.tagline,
            status = cached.status,
            imdbId = null
        )
    }
    
    private fun cachedToTvDetails(cached: CachedMediaDetails): TvDetails {
        val genreNames: List<String> = gson.fromJson(cached.genres, object : TypeToken<List<String>>() {}.type)
        val genreIds: List<Int> = gson.fromJson(cached.genreIds, object : TypeToken<List<Int>>() {}.type)
        val genres = genreNames.zip(genreIds).map { Genre(it.second, it.first) }
        
        return TvDetails(
            id = cached.id,
            name = cached.title,
            originalName = cached.originalTitle ?: cached.title,
            overview = cached.overview,
            posterPath = cached.posterPath,
            backdropPath = cached.backdropPath,
            firstAirDate = cached.releaseDate,
            lastAirDate = null,
            rating = cached.voteAverage,
            voteCount = cached.voteCount,
            numberOfSeasons = cached.numberOfSeasons ?: 0,
            numberOfEpisodes = cached.numberOfEpisodes ?: 0,
            episodeRunTime = cached.runtime?.let { listOf(it) },
            genres = genres,
            tagline = cached.tagline,
            status = cached.status
        )
    }
}

