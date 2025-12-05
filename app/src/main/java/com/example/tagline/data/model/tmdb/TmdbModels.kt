package com.example.tagline.data.model.tmdb

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val page: Int,
    val results: List<SearchResult>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResult(
    val id: Int,
    @SerializedName("media_type") val mediaType: String, // "movie" or "tv"
    val title: String? = null, // for movies
    val name: String? = null, // for TV shows
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null, // for movies
    @SerializedName("first_air_date") val firstAirDate: String? = null, // for TV shows
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("vote_count") val voteCount: Int? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    val popularity: Double? = null
) {
    val displayTitle: String
        get() = title ?: name ?: "Unknown"
    
    val displayDate: String?
        get() = releaseDate ?: firstAirDate
    
    val year: String?
        get() = displayDate?.take(4)
}

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    @SerializedName("original_title") val originalTitle: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?,
    @SerializedName("imdb_id") val imdbId: String?
)

data class TvDetailsResponse(
    val id: Int,
    val name: String,
    @SerializedName("original_name") val originalName: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("last_air_date") val lastAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?
)

data class Genre(
    val id: Int,
    val name: String
)

data class GenresResponse(
    val genres: List<Genre>
)

data class WatchProvidersResponse(
    val id: Int,
    val results: Map<String, CountryWatchProviders>?
)

data class CountryWatchProviders(
    val link: String?,
    val flatrate: List<WatchProvider>?, // Subscription services (Netflix, etc.)
    val rent: List<WatchProvider>?, // Rent options
    val buy: List<WatchProvider>?, // Buy options
    val free: List<WatchProvider>? // Free with ads
)

data class WatchProvider(
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int
)

