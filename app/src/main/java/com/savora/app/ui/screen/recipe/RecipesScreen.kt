package com.savora.app.ui.screen.recipe

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.savora.app.navigation.BottomNavBar
import com.savora.app.ui.components.RecipeCard
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.RecipeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    onRecipeClick: (String) -> Unit = {},
    navController: NavController,
    viewModel: RecipeListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadRecipes() }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = SavoraSurface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Resep Masakan",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = SavoraPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${uiState.displayedRecipes.size} resep",
                    style = MaterialTheme.typography.labelMedium,
                    color = SavoraOnSurfaceVariant,
                )
            }

            // Search Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SavoraSurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Search, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearch(it) },
                        placeholder = {
                            Text("Cari resep...", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = MaterialTheme.typography.labelMedium.copy(color = SavoraOnSurface),
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearch("") }) {
                                    Icon(Icons.Filled.Close, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                viewModel.categories.forEach { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterByCategory(cat) },
                        label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SavoraPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SavoraSurfaceContainerLow,
                            labelColor = SavoraOnSurfaceVariant,
                        ),
                        border = null,
                    )
                }
            }

            // Content
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = SavoraPrimary)
                }

                uiState.displayedRecipes.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.SearchOff, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (uiState.searchQuery.isNotEmpty())
                                "Resep \"${uiState.searchQuery}\" tidak ditemukan"
                            else "Belum ada resep",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SavoraOnSurfaceVariant,
                        )
                    }
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    uiState.displayedRecipes.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            pair.forEach { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    modifier = Modifier.weight(1f),
                                    isSaved = uiState.savedRecipeIds.contains(recipe.id),
                                    showCookTime = true,
                                    onClick = { onRecipeClick(recipe.id) },
                                    onToggleSave = { viewModel.toggleSaveRecipe(recipe.id) },
                                )
                            }
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
