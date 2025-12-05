package com.example.tagline.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tagline.data.local.entity.SearchHistoryItem
import com.example.tagline.data.model.tmdb.SearchResult
import com.example.tagline.ui.theme.PrimaryCrimson
import com.example.tagline.ui.theme.RatingHigh
import com.example.tagline.ui.theme.RatingLow
import com.example.tagline.ui.theme.RatingMedium
import com.example.tagline.ui.theme.SecondaryGold
import com.example.tagline.util.toFullPosterUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToMovieDetails: (Int) -> Unit,
    onNavigateToTvDetails: (Int) -> Unit,
    onNavigateToMyList: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Terminar Sessão") },
            text = { Text("Tens a certeza que queres sair?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sair", color = PrimaryCrimson)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Limpar Histórico") },
            text = { Text("Tens a certeza que queres apagar todo o histórico de pesquisas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearSearchHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Limpar", color = PrimaryCrimson)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
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
                        text = "TAGLINE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCrimson
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToMyList) {
                        Icon(
                            imageVector = Icons.Default.Bookmarks,
                            contentDescription = "Minha Lista",
                            tint = SecondaryGold
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sair"
                        )
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
            // Search bar
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::updateQuery,
                onSearch = viewModel::search,
                onFocus = viewModel::showSearchHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

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
                                text = uiState.errorMessage ?: "Erro desconhecido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                uiState.showHistory && uiState.searchHistory.isNotEmpty() -> {
                    // Show search history
                    SearchHistorySection(
                        history = uiState.searchHistory,
                        onItemClick = viewModel::searchFromHistory,
                        onDeleteItem = viewModel::deleteFromHistory,
                        onClearAll = { showClearHistoryDialog = true }
                    )
                }
                !uiState.hasSearched && uiState.searchHistory.isEmpty() -> {
                    // Initial state - show hint (no history)
                    EmptySearchState()
                }
                !uiState.hasSearched && uiState.searchHistory.isNotEmpty() -> {
                    // Show history if available
                    SearchHistorySection(
                        history = uiState.searchHistory,
                        onItemClick = viewModel::searchFromHistory,
                        onDeleteItem = viewModel::deleteFromHistory,
                        onClearAll = { showClearHistoryDialog = true }
                    )
                }
                uiState.results.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhum resultado encontrado",
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
                        items(uiState.results) { result ->
                            SearchResultCard(
                                result = result,
                                isSaved = viewModel.isItemSaved(result.id),
                                onClick = {
                                    if (result.mediaType == "movie") {
                                        onNavigateToMovieDetails(result.id)
                                    } else {
                                        onNavigateToTvDetails(result.id)
                                    }
                                },
                                onAddClick = {
                                    viewModel.addToList(result)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pesquisa filmes e séries",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Adiciona-os à tua lista para nunca perderes nada!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<SearchHistoryItem>,
    onItemClick: (SearchHistoryItem) -> Unit,
    onDeleteItem: (SearchHistoryItem) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pesquisas recentes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            TextButton(onClick = onClearAll) {
                Text(
                    text = "Limpar tudo",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryCrimson
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(history) { item ->
                SearchHistoryItemRow(
                    item = item,
                    onClick = { onItemClick(item) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }
    }
}

@Composable
private fun SearchHistoryItemRow(
    item: SearchHistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.query,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remover",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Pesquisar filmes e séries...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpar"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryCrimson,
            cursorColor = PrimaryCrimson
        )
    )
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    isSaved: Boolean,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Poster
            Box(
                modifier = Modifier
                    .width(95.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = result.posterPath.toFullPosterUrl(),
                    contentDescription = result.displayTitle,
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
                            color = if (result.mediaType == "movie") PrimaryCrimson else SecondaryGold,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (result.mediaType == "movie") "FILME" else "SÉRIE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
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
                        text = result.displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    result.year?.let { year ->
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
                    result.voteAverage?.let { rating ->
                        val ratingColor = when {
                            rating >= 7.0 -> RatingHigh
                            rating >= 5.0 -> RatingMedium
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
                            text = String.format("%.1f", rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ratingColor
                        )
                    }
                }
            }

            // Add button
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onAddClick,
                    enabled = !isSaved
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkAdd,
                        contentDescription = if (isSaved) "Já guardado" else "Adicionar à lista",
                        tint = if (isSaved) SecondaryGold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
