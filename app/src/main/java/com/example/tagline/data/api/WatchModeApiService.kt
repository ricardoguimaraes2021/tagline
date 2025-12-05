package com.example.tagline.data.api

import com.example.tagline.data.model.watchmode.TitleDetailsResponse
import com.example.tagline.data.model.watchmode.TitleSourcesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WatchModeApiService {

    @GET("title/{id}/sources/")
    suspend fun getTitleSources(
        @Path("id") titleId: String,
        @Query("regions") regions: String = "PT"
    ): List<TitleSourcesResponse>

    @GET("title/{id}/details/")
    suspend fun getTitleDetails(
        @Path("id") titleId: String
    ): TitleDetailsResponse

    @GET("search/")
    suspend fun searchTitles(
        @Query("search_field") searchField: String = "name",
        @Query("search_value") searchValue: String
    ): List<TitleDetailsResponse>

    companion object {
        const val BASE_URL = "https://api.watchmode.com/v1/"
    }
}

