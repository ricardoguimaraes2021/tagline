package com.example.tagline.data.model.watchmode

import com.google.gson.annotations.SerializedName

data class TitleSourcesResponse(
    @SerializedName("source_id") val sourceId: Int,
    val name: String,
    val type: String, // "sub", "rent", "buy", "free"
    val region: String,
    @SerializedName("ios_url") val iosUrl: String?,
    @SerializedName("android_url") val androidUrl: String?,
    @SerializedName("web_url") val webUrl: String?,
    val format: String?, // "HD", "SD", "4K"
    val price: Double?,
    val seasons: Int?,
    val episodes: Int?
)

data class TitleDetailsResponse(
    val id: Int,
    val title: String,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("plot_overview") val plotOverview: String?,
    val type: String, // "movie" or "tv_series"
    @SerializedName("runtime_minutes") val runtimeMinutes: Int?,
    val year: Int?,
    @SerializedName("end_year") val endYear: Int?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("tmdb_type") val tmdbType: String?,
    val genres: List<Int>?,
    @SerializedName("genre_names") val genreNames: List<String>?,
    @SerializedName("user_rating") val userRating: Double?,
    @SerializedName("critic_score") val criticScore: Int?,
    @SerializedName("us_rating") val usRating: String?,
    val poster: String?,
    val backdrop: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("similar_titles") val similarTitles: List<Int>?,
    val networks: List<Int>?,
    @SerializedName("network_names") val networkNames: List<String>?
)

