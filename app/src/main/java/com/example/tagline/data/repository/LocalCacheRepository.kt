package com.example.tagline.data.repository

import com.example.tagline.data.local.dao.CachedMediaDao
import com.example.tagline.data.local.dao.GenreDao
import com.example.tagline.data.local.dao.SearchHistoryDao
import com.example.tagline.data.local.entity.CachedGenre
import com.example.tagline.data.local.entity.CachedMediaDetails
import com.example.tagline.data.local.entity.SearchHistoryItem
import com.example.tagline.data.model.tmdb.Genre
import com.example.tagline.data.model.tmdb.MovieDetailsResponse
import com.example.tagline.data.model.tmdb.TvDetailsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCacheRepository @Inject constructor(
    private val genreDao: GenreDao,
    private val cachedMediaDao: CachedMediaDao,
    private val searchHistoryDao: SearchHistoryDao
) {
    private val gson = Gson()

    // ==================== GENRES ====================
    
    fun getAllGenres(): Flow<List<Genre>> {
        return genreDao.getAllGenres().map { cachedGenres ->
            cachedGenres.map { Genre(it.id, it.name) }
        }
    }
    
    suspend fun cacheGenres(genres: List<Genre>, type: String) {
        val cachedGenres = genres.map { CachedGenre(it.id, it.name, type) }
        genreDao.insertAll(cachedGenres)
    }
    
    suspend fun hasGenresCached(): Boolean {
        return genreDao.getCount() > 0
    }
    
    suspend fun getGenreNamesByIds(ids: List<Int>): List<String> {
        return genreDao.getGenreNamesByIds(ids)
    }

    // ==================== MEDIA DETAILS ====================
    
    suspend fun getCachedMovieDetails(movieId: Int): CachedMediaDetails? {
        val cached = cachedMediaDao.getMediaDetails(movieId, "movie")
        return if (cached != null && !cached.isExpired()) cached else null
    }
    
    suspend fun getCachedTvDetails(seriesId: Int): CachedMediaDetails? {
        val cached = cachedMediaDao.getMediaDetails(seriesId, "tv")
        return if (cached != null && !cached.isExpired()) cached else null
    }
    
    suspend fun cacheMovieDetails(movie: MovieDetailsResponse) {
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
    
    suspend fun cacheTvDetails(tv: TvDetailsResponse) {
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
    
    suspend fun clearExpiredCache() {
        val threshold = System.currentTimeMillis() - CachedMediaDetails.CACHE_VALIDITY_MS
        cachedMediaDao.deleteExpired(threshold)
    }
    
    // Helper to convert cached details to response objects
    fun cachedToMovieDetails(cached: CachedMediaDetails): MovieDetailsResponse {
        val genreNames: List<String> = gson.fromJson(cached.genres, object : TypeToken<List<String>>() {}.type)
        val genreIds: List<Int> = gson.fromJson(cached.genreIds, object : TypeToken<List<Int>>() {}.type)
        val genres = genreNames.zip(genreIds).map { Genre(it.second, it.first) }
        
        return MovieDetailsResponse(
            id = cached.id,
            title = cached.title,
            originalTitle = cached.originalTitle ?: cached.title,
            overview = cached.overview,
            posterPath = cached.posterPath,
            backdropPath = cached.backdropPath,
            releaseDate = cached.releaseDate,
            voteAverage = cached.voteAverage,
            voteCount = cached.voteCount,
            runtime = cached.runtime,
            genres = genres,
            tagline = cached.tagline,
            status = cached.status,
            imdbId = null
        )
    }
    
    fun cachedToTvDetails(cached: CachedMediaDetails): TvDetailsResponse {
        val genreNames: List<String> = gson.fromJson(cached.genres, object : TypeToken<List<String>>() {}.type)
        val genreIds: List<Int> = gson.fromJson(cached.genreIds, object : TypeToken<List<Int>>() {}.type)
        val genres = genreNames.zip(genreIds).map { Genre(it.second, it.first) }
        
        return TvDetailsResponse(
            id = cached.id,
            name = cached.title,
            originalName = cached.originalTitle ?: cached.title,
            overview = cached.overview,
            posterPath = cached.posterPath,
            backdropPath = cached.backdropPath,
            firstAirDate = cached.releaseDate,
            lastAirDate = null,
            voteAverage = cached.voteAverage,
            voteCount = cached.voteCount,
            numberOfSeasons = cached.numberOfSeasons ?: 0,
            numberOfEpisodes = cached.numberOfEpisodes ?: 0,
            episodeRunTime = cached.runtime?.let { listOf(it) },
            genres = genres,
            tagline = cached.tagline,
            status = cached.status
        )
    }

    // ==================== SEARCH HISTORY ====================
    
    fun getRecentSearches(): Flow<List<SearchHistoryItem>> {
        return searchHistoryDao.getRecentSearches()
    }
    
    suspend fun addSearchQuery(query: String) {
        // First try to update existing query's timestamp
        val updated = searchHistoryDao.updateTimestamp(query)
        if (updated == 0) {
            // Query doesn't exist, insert new one
            searchHistoryDao.insert(SearchHistoryItem(query = query))
        }
        // Trim old entries
        searchHistoryDao.trimHistory()
    }
    
    suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteByQuery(query)
    }
    
    suspend fun clearSearchHistory() {
        searchHistoryDao.deleteAll()
    }
}

