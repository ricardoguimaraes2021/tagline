package com.example.tagline.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.domain.model.CountryWatchProviders
import com.example.tagline.domain.model.Genre
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.usecase.AddToListUseCase
import com.example.tagline.domain.usecase.CheckItemSavedUseCase
import com.example.tagline.domain.usecase.GetMovieDetailsUseCase
import com.example.tagline.domain.usecase.GetTvDetailsUseCase
import com.example.tagline.domain.usecase.GetWatchProvidersUseCase
import com.example.tagline.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val title: String = "",
    val originalTitle: String = "",
    val overview: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val releaseDate: String? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val runtime: String? = null,
    val genres: List<Genre> = emptyList(),
    val tagline: String? = null,
    val status: String? = null,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val watchProviders: CountryWatchProviders? = null,
    val isSaved: Boolean = false,
    val isAddingToList: Boolean = false,
    val mediaType: MediaType = MediaType.MOVIE
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getTvDetailsUseCase: GetTvDetailsUseCase,
    private val getWatchProvidersUseCase: GetWatchProvidersUseCase,
    private val addToListUseCase: AddToListUseCase,
    private val checkItemSavedUseCase: CheckItemSavedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private var currentTmdbId: Int = 0

    fun loadMovieDetails(movieId: Int) {
        currentTmdbId = movieId
        _uiState.value = DetailsUiState(isLoading = true, mediaType = MediaType.MOVIE)

        viewModelScope.launch {
            // Check if saved
            val isSaved = checkItemSavedUseCase(movieId, MediaType.MOVIE)
            _uiState.value = _uiState.value.copy(isSaved = isSaved)
        }

        // Load movie details
        getMovieDetailsUseCase(movieId)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val movie = result.data!!
                        _uiState.value.copy(
                            isLoading = false,
                            title = movie.title,
                            originalTitle = movie.originalTitle,
                            overview = movie.overview,
                            posterPath = movie.posterPath,
                            backdropPath = movie.backdropPath,
                            releaseDate = movie.releaseDate,
                            rating = movie.rating,
                            voteCount = movie.voteCount,
                            runtime = movie.formattedRuntime,
                            genres = movie.genres,
                            tagline = movie.tagline,
                            status = movie.status
                        )
                    }
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)

        // Load watch providers
        getWatchProvidersUseCase(movieId, MediaType.MOVIE)
            .onEach { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(watchProviders = result.data)
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadTvDetails(seriesId: Int) {
        currentTmdbId = seriesId
        _uiState.value = DetailsUiState(isLoading = true, mediaType = MediaType.TV)

        viewModelScope.launch {
            // Check if saved
            val isSaved = checkItemSavedUseCase(seriesId, MediaType.TV)
            _uiState.value = _uiState.value.copy(isSaved = isSaved)
        }

        // Load TV details
        getTvDetailsUseCase(seriesId)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Loading -> _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val tv = result.data!!
                        _uiState.value.copy(
                            isLoading = false,
                            title = tv.name,
                            originalTitle = tv.originalName,
                            overview = tv.overview,
                            posterPath = tv.posterPath,
                            backdropPath = tv.backdropPath,
                            releaseDate = tv.firstAirDate,
                            rating = tv.rating,
                            voteCount = tv.voteCount,
                            runtime = tv.formattedRuntime,
                            genres = tv.genres,
                            tagline = tv.tagline,
                            status = tv.status,
                            numberOfSeasons = tv.numberOfSeasons,
                            numberOfEpisodes = tv.numberOfEpisodes
                        )
                    }
                    is Resource.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
            .launchIn(viewModelScope)

        // Load watch providers
        getWatchProvidersUseCase(seriesId, MediaType.TV)
            .onEach { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(watchProviders = result.data)
                }
            }
            .launchIn(viewModelScope)
    }

    fun addToList() {
        val state = _uiState.value
        if (state.isSaved || state.isAddingToList) return

        _uiState.value = _uiState.value.copy(isAddingToList = true)

        val item = SavedMedia(
            tmdbId = currentTmdbId,
            title = state.title,
            type = state.mediaType,
            posterPath = state.posterPath,
            backdropPath = state.backdropPath,
            rating = state.rating,
            genres = state.genres.map { it.name },
            genreIds = state.genres.map { it.id },
            overview = state.overview,
            releaseYear = state.releaseDate?.take(4)
        )

        addToListUseCase(item)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Success -> _uiState.value.copy(
                        isSaved = true,
                        isAddingToList = false
                    )
                    is Resource.Error -> _uiState.value.copy(
                        isAddingToList = false,
                        errorMessage = result.message
                    )
                    is Resource.Loading -> _uiState.value
                }
            }
            .launchIn(viewModelScope)
    }
}
