package com.example.tagline.util

object Constants {
    // TMDB Image URLs
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val POSTER_SIZE_SMALL = "w185"
    const val POSTER_SIZE_MEDIUM = "w342"
    const val POSTER_SIZE_LARGE = "w500"
    const val POSTER_SIZE_ORIGINAL = "original"
    const val BACKDROP_SIZE = "w780"
    
    // Watch Provider Logos
    const val PROVIDER_LOGO_SIZE = "w92"
    
    // Pagination
    const val ITEMS_PER_PAGE = 20
    
    // Countries for Watch Providers
    const val DEFAULT_COUNTRY = "PT"
    val SUPPORTED_COUNTRIES = listOf("PT", "BR", "US", "GB", "ES", "FR", "DE")
}

fun String?.toFullPosterUrl(size: String = Constants.POSTER_SIZE_MEDIUM): String? {
    return this?.let { "${Constants.TMDB_IMAGE_BASE_URL}$size$it" }
}

fun String?.toFullBackdropUrl(): String? {
    return this?.let { "${Constants.TMDB_IMAGE_BASE_URL}${Constants.BACKDROP_SIZE}$it" }
}

fun String?.toProviderLogoUrl(): String? {
    return this?.let { "${Constants.TMDB_IMAGE_BASE_URL}${Constants.PROVIDER_LOGO_SIZE}$it" }
}

