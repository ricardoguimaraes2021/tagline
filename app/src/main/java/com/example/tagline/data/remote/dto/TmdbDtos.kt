package com.example.tagline.data.remote.dto

import com.example.tagline.domain.model.Genre
import com.example.tagline.domain.model.Media
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.MovieDetails
import com.example.tagline.domain.model.SearchResult
import com.example.tagline.domain.model.TvDetails
import com.google.gson.annotations.SerializedName

// ==================== SEARCH ====================

data class SearchResponseDto(
    val page: Int,
    val results: List<SearchResultDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResultDto(
    val id: Int,
    @SerializedName("media_type") val mediaType: String,
    val title: String? = null,
    val name: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("original_name") val originalName: String? = null,
    val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("vote_count") val voteCount: Int? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    val popularity: Double? = null
)

// ==================== MOVIE DETAILS ====================

data class MovieDetailsDto(
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
    val genres: List<GenreDto>,
    val tagline: String?,
    val status: String?,
    @SerializedName("imdb_id") val imdbId: String?
)

// ==================== TV DETAILS ====================

data class TvDetailsDto(
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
    val genres: List<GenreDto>,
    val tagline: String?,
    val status: String?
)

// ==================== GENRES ====================

data class GenreDto(
    val id: Int,
    val name: String
)

data class GenresResponseDto(
    val genres: List<GenreDto>
)

// ==================== WATCH PROVIDERS ====================

data class WatchProvidersResponseDto(
    val id: Int,
    val results: Map<String, CountryWatchProvidersDto>?
)

data class CountryWatchProvidersDto(
    val link: String?,
    val flatrate: List<WatchProviderDto>?,
    val rent: List<WatchProviderDto>?,
    val buy: List<WatchProviderDto>?,
    val free: List<WatchProviderDto>?
)

data class WatchProviderDto(
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int
)

// ==================== MAPPERS ====================

fun SearchResponseDto.toSearchResult(): SearchResult {
    return SearchResult(
        page = page,
        results = results
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }
            .map { it.toMedia() },
        totalPages = totalPages,
        totalResults = totalResults
    )
}

fun SearchResultDto.toMedia(): Media {
    return Media(
        id = id,
        title = title ?: name ?: "Unknown",
        originalTitle = originalTitle ?: originalName,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate ?: firstAirDate,
        rating = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        mediaType = MediaType.fromString(mediaType),
        genreIds = genreIds ?: emptyList()
    )
}

fun MovieDetailsDto.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genres.map { it.toGenre() },
        tagline = tagline,
        status = status,
        imdbId = imdbId
    )
}

fun TvDetailsDto.toTvDetails(): TvDetails {
    return TvDetails(
        id = id,
        name = name,
        originalName = originalName,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        lastAirDate = lastAirDate,
        rating = voteAverage,
        voteCount = voteCount,
        numberOfSeasons = numberOfSeasons,
        numberOfEpisodes = numberOfEpisodes,
        episodeRunTime = episodeRunTime,
        genres = genres.map { it.toGenre() },
        tagline = tagline,
        status = status
    )
}

fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun WatchProviderDto.toWatchProvider(): com.example.tagline.domain.model.WatchProvider {
    return com.example.tagline.domain.model.WatchProvider(
        id = providerId,
        name = providerName,
        logoPath = logoPath,
        displayPriority = displayPriority
    )
}

fun CountryWatchProvidersDto.toCountryWatchProviders(): com.example.tagline.domain.model.CountryWatchProviders {
    return com.example.tagline.domain.model.CountryWatchProviders(
        link = link,
        flatrate = flatrate?.map { it.toWatchProvider() } ?: emptyList(),
        rent = rent?.map { it.toWatchProvider() } ?: emptyList(),
        buy = buy?.map { it.toWatchProvider() } ?: emptyList(),
        free = free?.map { it.toWatchProvider() } ?: emptyList()
    )
}

