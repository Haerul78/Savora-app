package com.savora.app.ui.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.SavedRecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    onNavigateBack: () -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    viewModel: SavedRecipesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SavoraSurface,
        topBar = {
            TopAppBar(
                title = { Text("Resep Tersimpan", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Kembali") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SavoraSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Category filter
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory == cat,
                        onClick = { viewModel.filterByCategory(cat) },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SavoraPrimary, selectedLabelColor = Color.White,
                            containerColor = SavoraSurfaceContainerLow, labelColor = SavoraOnSurfaceVariant
                        ), border = null
                    )
                }
            }

            Text("${uiState.totalCount} Resep tersimpan",
                style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = SavoraPrimary) }
            } else {
                Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
                    val pairs = uiState.savedRecipes.chunked(2)
                    pairs.forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { recipe ->
                                Surface(
                                    modifier = Modifier.weight(1f).clickable { onRecipeClick(recipe.id) },
                                    shape = RoundedCornerShape(16.dp), color = SavoraSurfaceContainerLowest
                                ) {
                                    Column {
                                        Box {
                                            AsyncImage(
                                                model = recipe.imageUrl, contentDescription = recipe.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxWidth().height(120.dp)
                                                    .clip(RoundedCornerShape(24.dp)).padding(4.dp)
                                            )
                                            IconButton(
                                                onClick = { viewModel.unsaveRecipe(recipe.id) },
                                                modifier = Modifier.align(Alignment.TopEnd)
                                            ) {
                                                Icon(Icons.Filled.Bookmark, null, tint = SavoraPrimary)
                                            }
                                        }
                                        Column(Modifier.padding(12.dp)) {
                                            Text(recipe.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Star, null, tint = SavoraStar, modifier = Modifier.size(14.dp))
                                                Text(" ${recipe.rating} (${recipe.totalReviews})", style = MaterialTheme.typography.labelMedium)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Schedule, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(14.dp))
                                                Text(" ${recipe.cookTimeMinutes} menit", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
