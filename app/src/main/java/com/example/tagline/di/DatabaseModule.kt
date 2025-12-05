package com.example.tagline.di

import android.content.Context
import androidx.room.Room
import com.example.tagline.data.local.AppDatabase
import com.example.tagline.data.local.dao.CachedMediaDao
import com.example.tagline.data.local.dao.GenreDao
import com.example.tagline.data.local.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideGenreDao(database: AppDatabase): GenreDao {
        return database.genreDao()
    }
    
    @Provides
    @Singleton
    fun provideCachedMediaDao(database: AppDatabase): CachedMediaDao {
        return database.cachedMediaDao()
    }
    
    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}

