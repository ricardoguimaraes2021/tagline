package com.example.tagline.di

import com.example.tagline.data.repository.AuthRepositoryImpl
import com.example.tagline.data.repository.MediaRepositoryImpl
import com.example.tagline.data.repository.SavedMediaRepositoryImpl
import com.example.tagline.data.repository.SearchHistoryRepositoryImpl
import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.domain.repository.MediaRepository
import com.example.tagline.domain.repository.SavedMediaRepository
import com.example.tagline.domain.repository.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindSavedMediaRepository(
        savedMediaRepositoryImpl: SavedMediaRepositoryImpl
    ): SavedMediaRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository
}

