package com.example.tagline.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.data.local.entity.SearchHistoryItem
import com.example.tagline.data.model.MediaType
import com.example.tagline.data.model.SavedItem
import com.example.tagline.data.model.tmdb.SearchResult
import com.example.tagline.data.repository.AuthRepository
import com.example.tagline.data.repository.LocalCacheRepository
import com.example.tagline.data.repository.SavedItemsRepository
import com.example.tagline.data.repository.TmdbRepository
import com.example.tagline.util.Resource
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
    val savedItemIds: Set<Int> = emptySet(),
    val searchHistory: List<SearchHistoryItem> = emptyList(),
    val showHistory: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tmdbRepository: TmdbRepository,
    private val savedItemsRepository: SavedItemsRepository,
    private val authRepository: AuthRepository,
    private val localCacheRepository: LocalCacheRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSavedItemIds()
        loadSearchHistory()
    }

    private fun loadSavedItemIds() {
        viewModelScope.launch {
            savedItemsRepository.getSavedItems().collect { result ->
                if (result is Resource.Success) {
                    val ids = result.data?.map { it.tmdbId }?.toSet() ?: emptySet()
                    _uiState.value = _uiState.value.copy(savedItemIds = ids)
                }
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            localCacheRepository.getRecentSearches().collect { history ->
                _uiState.value = _uiState.value.copy(searchHistory = history)
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query,
            showHistory = query.isEmpty() && _uiState.value.searchHistory.isNotEmpty()
        )
        
        // Debounce search
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(500) // Wait for user to stop typing
                search()
            }
        } else if (query.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                results = emptyList(),
                hasSearched = false,
                showHistory = _uiState.value.searchHistory.isNotEmpty()
            )
        }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                showHistory = false
            )

            when (val result = tmdbRepository.searchMulti(query)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        results = result.data?.results ?: emptyList(),
                        isLoading = false,
                        hasSearched = true
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        hasSearched = true
                    )
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun searchFromHistory(historyItem: SearchHistoryItem) {
        _uiState.value = _uiState.value.copy(query = historyItem.query)
        search()
    }

    fun deleteFromHistory(historyItem: SearchHistoryItem) {
        viewModelScope.launch {
            localCacheRepository.deleteSearchQuery(historyItem.query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            localCacheRepository.clearSearchHistory()
        }
    }

    fun showSearchHistory() {
        if (_uiState.value.searchHistory.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(showHistory = true)
        }
    }

    fun hideSearchHistory() {
        _uiState.value = _uiState.value.copy(showHistory = false)
    }

    fun addToList(searchResult: SearchResult) {
        viewModelScope.launch {
            val mediaType = MediaType.fromString(searchResult.mediaType)
            val item = SavedItem(
                tmdbId = searchResult.id,
                title = searchResult.displayTitle,
                type = mediaType,
                posterPath = searchResult.posterPath,
                backdropPath = searchResult.backdropPath,
                rating = searchResult.voteAverage ?: 0.0,
                genres = emptyList(), // Will be populated from details screen
                genreIds = searchResult.genreIds ?: emptyList(),
                overview = searchResult.overview,
                releaseYear = searchResult.year,
                addedAt = Timestamp.now()
            )

            when (val result = savedItemsRepository.addItem(item)) {
                is Resource.Success -> {
                    // Update saved item IDs
                    _uiState.value = _uiState.value.copy(
                        savedItemIds = _uiState.value.savedItemIds + searchResult.id
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun isItemSaved(tmdbId: Int): Boolean {
        return tmdbId in _uiState.value.savedItemIds
    }

    fun logout() {
        authRepository.logout()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
