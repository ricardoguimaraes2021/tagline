package com.example.tagline.data.remote.api

import com.example.tagline.data.remote.dto.GenresResponseDto
import com.example.tagline.data.remote.dto.MovieDetailsDto
import com.example.tagline.data.remote.dto.SearchResponseDto
import com.example.tagline.data.remote.dto.TvDetailsDto
import com.example.tagline.data.remote.dto.WatchProvidersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-PT"
    ): SearchResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "pt-PT"
    ): MovieDetailsDto

    @GET("tv/{series_id}")
    suspend fun getTvDetails(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "pt-PT"
    ): TvDetailsDto

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int
    ): WatchProvidersResponseDto

    @GET("tv/{series_id}/watch/providers")
    suspend fun getTvWatchProviders(
        @Path("series_id") seriesId: Int
    ): WatchProvidersResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String = "pt-PT"
    ): GenresResponseDto

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("language") language: String = "pt-PT"
    ): GenresResponseDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        const val POSTER_SIZE = "w500"
        const val BACKDROP_SIZE = "w780"
    }
}

