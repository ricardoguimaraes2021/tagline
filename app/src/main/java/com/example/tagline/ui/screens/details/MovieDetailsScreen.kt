package com.example.tagline.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tagline.domain.model.WatchProvider
import com.example.tagline.ui.theme.*
import com.example.tagline.util.toFullBackdropUrl
import com.example.tagline.util.toFullPosterUrl
import com.example.tagline.util.toProviderLogoUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    DetailsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddToList = viewModel::addToList,
        mediaTypeLabel = "FILME"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvDetailsScreen(
    seriesId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(seriesId) {
        viewModel.loadTvDetails(seriesId)
    }

    DetailsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddToList = viewModel::addToList,
        mediaTypeLabel = "SÉRIE"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreenContent(
    uiState: DetailsUiState,
    onNavigateBack: () -> Unit,
    onAddToList: () -> Unit,
    mediaTypeLabel: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryCrimson)
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Backdrop with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = uiState.backdropPath.toFullBackdropUrl() 
                                ?: uiState.posterPath.toFullPosterUrl(),
                            contentDescription = uiState.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )

                        // Media type badge
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(top = paddingValues.calculateTopPadding())
                                .align(Alignment.TopEnd)
                                .background(
                                    color = if (mediaTypeLabel == "FILME") PrimaryCrimson else SecondaryGold,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mediaTypeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Title and rating row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (uiState.originalTitle != uiState.title) {
                                    Text(
                                        text = uiState.originalTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }

                            // Rating
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                val ratingColor = when {
                                    uiState.rating >= 7.0 -> RatingHigh
                                    uiState.rating >= 5.0 -> RatingMedium
                                    else -> RatingLow
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ratingColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%.1f", uiState.rating),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = ratingColor
                                    )
                                }
                                Text(
                                    text = "${uiState.voteCount} votos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Tagline
                        uiState.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                            Text(
                                text = "\"$tagline\"",
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                                color = SecondaryGold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            uiState.releaseDate?.take(4)?.let { year ->
                                InfoChip(icon = Icons.Default.CalendarMonth, text = year)
                            }
                            uiState.runtime?.let { runtime ->
                                InfoChip(icon = Icons.Default.Schedule, text = runtime)
                            }
                            uiState.numberOfSeasons?.let { seasons ->
                                InfoChip(
                                    icon = Icons.Default.Tv,
                                    text = "$seasons temporada${if (seasons > 1) "s" else ""}"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Genres
                        if (uiState.genres.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.genres) { genre ->
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text(genre.name) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Add to list button
                        Button(
                            onClick = onAddToList,
                            enabled = !uiState.isSaved && !uiState.isAddingToList,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isSaved) Success else PrimaryCrimson,
                                disabledContainerColor = if (uiState.isSaved) Success else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            if (uiState.isAddingToList) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (uiState.isSaved) Icons.Default.Check else Icons.Outlined.BookmarkAdd,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isSaved) "Na tua lista" else "Adicionar à lista",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Overview
                        uiState.overview?.takeIf { it.isNotBlank() }?.let { overview ->
                            Text(
                                text = "Sinopse",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        // Watch providers
                        uiState.watchProviders?.let { providers ->
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Onde Assistir (Portugal)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Subscription services
                            if (providers.flatrate.isNotEmpty()) {
                                WatchProviderSection(
                                    title = "Streaming",
                                    providers = providers.flatrate
                                )
                            }

                            // Rent
                            if (providers.rent.isNotEmpty()) {
                                WatchProviderSection(
                                    title = "Alugar",
                                    providers = providers.rent
                                )
                            }

                            // Buy
                            if (providers.buy.isNotEmpty()) {
                                WatchProviderSection(
                                    title = "Comprar",
                                    providers = providers.buy
                                )
                            }

                            // Free
                            if (providers.free.isNotEmpty()) {
                                WatchProviderSection(
                                    title = "Grátis",
                                    providers = providers.free
                                )
                            }

                            if (providers.flatrate.isEmpty() && 
                                providers.rent.isEmpty() && 
                                providers.buy.isEmpty() && 
                                providers.free.isEmpty()) {
                                Text(
                                    text = "Não disponível em plataformas de streaming em Portugal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WatchProviderSection(
    title: String,
    providers: List<WatchProvider>
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(providers) { provider ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = provider.logoPath.toProviderLogoUrl(),
                        contentDescription = provider.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 60.dp)
                    )
                }
            }
        }
    }
}
