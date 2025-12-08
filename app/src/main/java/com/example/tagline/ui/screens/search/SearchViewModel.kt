package com.example.tagline.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.domain.model.Media
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.repository.SearchHistoryItem
import com.example.tagline.domain.usecase.AddToListUseCase
import com.example.tagline.domain.usecase.DeleteSearchHistoryUseCase
import com.example.tagline.domain.usecase.GetSavedItemsUseCase
import com.example.tagline.domain.usecase.GetSearchHistoryUseCase
import com.example.tagline.domain.usecase.SearchMediaUseCase
import com.example.tagline.domain.repository.AuthRepository
import com.example.tagline.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Media> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
    val savedItemIds: Set<Int> = emptySet(),
    val searchHistory: List<SearchHistoryItem> = emptyList(),
    val showHistory: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase,
    private val getSavedItemsUseCase: GetSavedItemsUseCase,
    private val addToListUseCase: AddToListUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSavedItemIds()
        loadSearchHistory()
    }

    private fun loadSavedItemIds() {
        getSavedItemsUseCase()
            .onEach { result ->
                if (result is Resource.Success) {
                    val ids = result.data?.map { it.tmdbId }?.toSet() ?: emptySet()
                    _uiState.value = _uiState.value.copy(savedItemIds = ids)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadSearchHistory() {
        getSearchHistoryUseCase()
            .onEach { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(
                        searchHistory = result.data ?: emptyList()
                    )
                }
            }
            .launchIn(viewModelScope)
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

        searchMediaUseCase(query)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null,
                        showHistory = false
                    )
                    is Resource.Success -> _uiState.value.copy(
                        results = result.data?.results ?: emptyList(),
                        isLoading = false,
                        hasSearched = true
                    )
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        hasSearched = true
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchFromHistory(historyItem: SearchHistoryItem) {
        _uiState.value = _uiState.value.copy(query = historyItem.query)
        search()
    }

    fun deleteFromHistory(historyItem: SearchHistoryItem) {
        viewModelScope.launch {
            deleteSearchHistoryUseCase.deleteQuery(historyItem.query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            deleteSearchHistoryUseCase.clearAll()
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

    fun addToList(media: Media) {
        val item = SavedMedia(
            tmdbId = media.id,
            title = media.title,
            type = media.mediaType,
            posterPath = media.posterPath,
            backdropPath = media.backdropPath,
            rating = media.rating,
            genres = emptyList(),
            genreIds = media.genreIds,
            overview = media.overview,
            releaseYear = media.year
        )

        addToListUseCase(item)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            savedItemIds = _uiState.value.savedItemIds + media.id
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
            .launchIn(viewModelScope)
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
