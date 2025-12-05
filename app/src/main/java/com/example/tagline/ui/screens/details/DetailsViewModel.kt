package com.example.tagline.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagline.data.model.MediaType
import com.example.tagline.data.model.SavedItem
import com.example.tagline.data.model.tmdb.CountryWatchProviders
import com.example.tagline.data.model.tmdb.Genre
import com.example.tagline.data.model.tmdb.MovieDetailsResponse
import com.example.tagline.data.model.tmdb.TvDetailsResponse
import com.example.tagline.data.model.tmdb.WatchProvider
import com.example.tagline.data.repository.SavedItemsRepository
import com.example.tagline.data.repository.TmdbRepository
import com.example.tagline.util.Constants
import com.example.tagline.util.Resource
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val tmdbRepository: TmdbRepository,
    private val savedItemsRepository: SavedItemsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private var currentTmdbId: Int = 0

    fun loadMovieDetails(movieId: Int) {
        currentTmdbId = movieId
        _uiState.value = DetailsUiState(isLoading = true, mediaType = MediaType.MOVIE)

        viewModelScope.launch {
            // Check if saved
            val isSaved = savedItemsRepository.isItemSaved(movieId, MediaType.MOVIE)
            _uiState.value = _uiState.value.copy(isSaved = isSaved)

            // Load movie details
            when (val result = tmdbRepository.getMovieDetails(movieId)) {
                is Resource.Success -> {
                    result.data?.let { movie ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            title = movie.title,
                            originalTitle = movie.originalTitle,
                            overview = movie.overview,
                            posterPath = movie.posterPath,
                            backdropPath = movie.backdropPath,
                            releaseDate = movie.releaseDate,
                            rating = movie.voteAverage,
                            voteCount = movie.voteCount,
                            runtime = movie.runtime?.let { "${it}min" },
                            genres = movie.genres,
                            tagline = movie.tagline,
                            status = movie.status
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> { }
            }

            // Load watch providers
            when (val result = tmdbRepository.getMovieWatchProviders(movieId)) {
                is Resource.Success -> {
                    val providers = result.data?.results?.get(Constants.DEFAULT_COUNTRY)
                    _uiState.value = _uiState.value.copy(watchProviders = providers)
                }
                else -> { }
            }
        }
    }

    fun loadTvDetails(seriesId: Int) {
        currentTmdbId = seriesId
        _uiState.value = DetailsUiState(isLoading = true, mediaType = MediaType.TV)

        viewModelScope.launch {
            // Check if saved
            val isSaved = savedItemsRepository.isItemSaved(seriesId, MediaType.TV)
            _uiState.value = _uiState.value.copy(isSaved = isSaved)

            // Load TV details
            when (val result = tmdbRepository.getTvDetails(seriesId)) {
                is Resource.Success -> {
                    result.data?.let { tv ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            title = tv.name,
                            originalTitle = tv.originalName,
                            overview = tv.overview,
                            posterPath = tv.posterPath,
                            backdropPath = tv.backdropPath,
                            releaseDate = tv.firstAirDate,
                            rating = tv.voteAverage,
                            voteCount = tv.voteCount,
                            runtime = tv.episodeRunTime?.firstOrNull()?.let { "${it}min/ep" },
                            genres = tv.genres,
                            tagline = tv.tagline,
                            status = tv.status,
                            numberOfSeasons = tv.numberOfSeasons,
                            numberOfEpisodes = tv.numberOfEpisodes
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> { }
            }

            // Load watch providers
            when (val result = tmdbRepository.getTvWatchProviders(seriesId)) {
                is Resource.Success -> {
                    val providers = result.data?.results?.get(Constants.DEFAULT_COUNTRY)
                    _uiState.value = _uiState.value.copy(watchProviders = providers)
                }
                else -> { }
            }
        }
    }

    fun addToList() {
        val state = _uiState.value
        if (state.isSaved || state.isAddingToList) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToList = true)

            val item = SavedItem(
                tmdbId = currentTmdbId,
                title = state.title,
                type = state.mediaType,
                posterPath = state.posterPath,
                backdropPath = state.backdropPath,
                rating = state.rating,
                genres = state.genres.map { it.name },
                genreIds = state.genres.map { it.id },
                overview = state.overview,
                releaseYear = state.releaseDate?.take(4),
                addedAt = Timestamp.now()
            )

            when (val result = savedItemsRepository.addItem(item)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaved = true,
                        isAddingToList = false
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAddingToList = false,
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }
}

