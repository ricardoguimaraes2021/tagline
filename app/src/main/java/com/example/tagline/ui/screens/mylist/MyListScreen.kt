package com.example.tagline.ui.screens.mylist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tagline.data.model.MediaType
import com.example.tagline.data.model.SavedItem
import com.example.tagline.ui.theme.*
import com.example.tagline.util.toFullPosterUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMovieDetails: (Int) -> Unit,
    onNavigateToTvDetails: (Int) -> Unit,
    viewModel: MyListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var itemToDelete by remember { mutableStateOf<SavedItem?>(null) }

    // Delete confirmation dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Remover da Lista") },
            text = { Text("Tens a certeza que queres remover \"${item.title}\" da tua lista?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeItem(item)
                        itemToDelete = null
                    }
                ) {
                    Text("Remover", color = PrimaryCrimson)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "A Minha Lista",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (uiState.filterType != FilterType.ALL || 
                        uiState.selectedGenre != null || 
                        uiState.searchQuery.isNotBlank() ||
                        uiState.showWatchedOnly) {
                        IconButton(onClick = { viewModel.clearFilters() }) {
                            Icon(
                                imageVector = Icons.Default.FilterAltOff,
                                contentDescription = "Limpar filtros"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search and filters
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar na lista...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCrimson
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.ALL,
                            onClick = { viewModel.setFilterType(FilterType.ALL) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCrimson,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.MOVIES,
                            onClick = { viewModel.setFilterType(FilterType.MOVIES) },
                            label = { Text("Filmes") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCrimson,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.TV_SHOWS,
                            onClick = { viewModel.setFilterType(FilterType.TV_SHOWS) },
                            label = { Text("Séries") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SecondaryGold,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.showWatchedOnly,
                            onClick = { viewModel.toggleWatchedOnly() },
                            label = { Text("Vistos") },
                            leadingIcon = {
                                if (uiState.showWatchedOnly) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Success,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.filteredItems.size} itens",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                val watchedCount = uiState.filteredItems.count { it.watched }
                if (watchedCount > 0) {
                    Text(
                        text = "$watchedCount vistos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Success
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content
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
                                text = uiState.errorMessage ?: "Erro",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                uiState.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "A tua lista está vazia",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pesquisa filmes e séries para adicionar à lista",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                uiState.filteredItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhum resultado com estes filtros",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredItems,
                            key = { it.id }
                        ) { item ->
                            SavedItemCard(
                                item = item,
                                onClick = {
                                    if (item.type == MediaType.MOVIE) {
                                        onNavigateToMovieDetails(item.tmdbId)
                                    } else {
                                        onNavigateToTvDetails(item.tmdbId)
                                    }
                                },
                                onToggleWatched = { viewModel.toggleWatched(item) },
                                onDelete = { itemToDelete = item }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedItemCard(
    item: SavedItem,
    onClick: () -> Unit,
    onToggleWatched: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.watched) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // Poster
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = item.posterPath.toFullPosterUrl(),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Media type badge
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = if (item.type == MediaType.MOVIE) PrimaryCrimson else SecondaryGold,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (item.type == MediaType.MOVIE) "FILME" else "SÉRIE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Watched overlay
                if (item.watched) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Success.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Visto",
                            tint = Success,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (item.watched) TextDecoration.LineThrough else TextDecoration.None
                    )
                    
                    item.releaseYear?.let { year ->
                        Text(
                            text = year,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.rating > 0) {
                        val ratingColor = when {
                            item.rating >= 7.0 -> RatingHigh
                            item.rating >= 5.0 -> RatingMedium
                            else -> RatingLow
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ratingColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", item.rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ratingColor
                        )
                    }
                }
            }

            // Actions
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onToggleWatched) {
                    Icon(
                        imageVector = if (item.watched) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (item.watched) "Marcar como não visto" else "Marcar como visto",
                        tint = if (item.watched) Success else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

