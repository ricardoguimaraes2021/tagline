package com.example.tagline.di

import com.example.tagline.BuildConfig
import com.example.tagline.data.api.TmdbApiService
import com.example.tagline.data.api.WatchModeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    @Named("tmdb")
    fun provideTmdbAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @Named("watchmode")
    fun provideWatchModeAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("apiKey", BuildConfig.WATCHMODE_API_KEY)
                .build()
            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @Named("tmdb")
    fun provideTmdbOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("tmdb") authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("watchmode")
    fun provideWatchModeOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("watchmode") authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApiService(
        @Named("tmdb") okHttpClient: OkHttpClient
    ): TmdbApiService {
        return Retrofit.Builder()
            .baseUrl(TmdbApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWatchModeApiService(
        @Named("watchmode") okHttpClient: OkHttpClient
    ): WatchModeApiService {
        return Retrofit.Builder()
            .baseUrl(WatchModeApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WatchModeApiService::class.java)
    }
}

