package com.example.tagline.ui.screens.mylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.domain.model.Genre
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.usecase.GetAllGenresUseCase
import com.example.tagline.domain.usecase.GetSavedItemsUseCase
import com.example.tagline.domain.usecase.RemoveFromListUseCase
import com.example.tagline.domain.usecase.ToggleWatchedUseCase
import com.example.tagline.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

enum class FilterType {
    ALL, MOVIES, TV_SHOWS
}

data class MyListUiState(
    val items: List<SavedMedia> = emptyList(),
    val filteredItems: List<SavedMedia> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterType: FilterType = FilterType.ALL,
    val searchQuery: String = "",
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val showWatchedOnly: Boolean = false
)

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val getSavedItemsUseCase: GetSavedItemsUseCase,
    private val getAllGenresUseCase: GetAllGenresUseCase,
    private val toggleWatchedUseCase: ToggleWatchedUseCase,
    private val removeFromListUseCase: RemoveFromListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyListUiState())
    val uiState: StateFlow<MyListUiState> = _uiState.asStateFlow()

    init {
        loadItems()
        loadGenres()
    }

    private fun loadItems() {
        getSavedItemsUseCase()
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val items = result.data ?: emptyList()
                        _uiState.value.copy(
                            items = items,
                            isLoading = false,
                            errorMessage = null
                        ).also { applyFilters() }
                    }
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadGenres() {
        getAllGenresUseCase()
            .onEach { result ->
                if (result is Resource.Success) {
                    val genres = result.data?.map { it.name } ?: emptyList()
                    _uiState.value = _uiState.value.copy(genres = genres)
                }
            }
            .launchIn(viewModelScope)
    }

    fun setFilterType(filterType: FilterType) {
        _uiState.value = _uiState.value.copy(filterType = filterType)
        applyFilters()
    }

    fun setSelectedGenre(genre: String?) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleWatchedOnly() {
        _uiState.value = _uiState.value.copy(
            showWatchedOnly = !_uiState.value.showWatchedOnly
        )
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.items

        // Filter by type
        filtered = when (state.filterType) {
            FilterType.ALL -> filtered
            FilterType.MOVIES -> filtered.filter { it.type == MediaType.MOVIE }
            FilterType.TV_SHOWS -> filtered.filter { it.type == MediaType.TV }
        }

        // Filter by genre
        state.selectedGenre?.let { genre ->
            filtered = filtered.filter { item ->
                item.genres.contains(genre) || item.genreIds.isNotEmpty()
            }
        }

        // Filter by search query
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { item ->
                item.title.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Filter by watched status
        if (state.showWatchedOnly) {
            filtered = filtered.filter { it.watched }
        }

        _uiState.value = _uiState.value.copy(filteredItems = filtered)
    }

    fun toggleWatched(item: SavedMedia) {
        toggleWatchedUseCase(item.id, !item.watched)
            .launchIn(viewModelScope)
    }

    fun removeItem(item: SavedMedia) {
        removeFromListUseCase(item.id)
            .launchIn(viewModelScope)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            filterType = FilterType.ALL,
            selectedGenre = null,
            searchQuery = "",
            showWatchedOnly = false
        )
        applyFilters()
    }
}
