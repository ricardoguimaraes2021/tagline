package com.example.tagline.domain.usecase

import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.repository.SavedMediaRepository
import javax.inject.Inject

/**
 * Use case for checking if a media item is saved.
 * Simple synchronous check - no Flow needed.
 */
class CheckItemSavedUseCase @Inject constructor(
    private val savedMediaRepository: SavedMediaRepository
) {
    suspend operator fun invoke(tmdbId: Int, mediaType: MediaType): Boolean {
        return try {
            savedMediaRepository.isItemSaved(tmdbId, mediaType)
        } catch (e: Exception) {
            false
        }
    }
}

