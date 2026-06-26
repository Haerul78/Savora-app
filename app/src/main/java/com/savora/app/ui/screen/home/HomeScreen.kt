package com.savora.app.ui.screen.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.savora.app.navigation.BottomNavBar
import com.savora.app.ui.components.ProductCard
import com.savora.app.ui.components.RecipeCard
import com.savora.app.ui.components.SavoraTopBar
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.CartViewModel
import com.savora.app.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {},
    onNavigateToStore: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadData() }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = SavoraSurface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SavoraTopBar(
                cityLabel = uiState.primaryAddress?.city ?: "Tentukan alamat",
                cartCount = cartUiState.cartItems.sumOf { it.cartItem.quantity },
                onCartClick = onCartClick,
            )

            // Search Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SavoraSurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp),
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp),
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

            // Category Chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                viewModel.categories.forEach { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterByCategory(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelMedium) },
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

            Spacer(modifier = Modifier.height(8.dp))

            // Section Rekomendasi
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Rekomendasi Untukmu", style = MaterialTheme.typography.headlineSmall, color = SavoraOnSurface)
                Text(
                    "Lihat Semua",
                    style = MaterialTheme.typography.labelMedium,
                    color = SavoraPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToRecipes() },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SavoraPrimary)
                }
            } else {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.displayedRecipes.take(10).forEach { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            modifier = Modifier.width(180.dp),
                            isSaved = uiState.savedRecipeIds.contains(recipe.id),
                            onClick = { onRecipeClick(recipe.id) },
                            onToggleSave = { viewModel.toggleSaveRecipe(recipe.id) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Bahan Segar
            Box(modifier = Modifier.background(SavoraSurfaceContainerLow).padding(vertical = 16.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Bahan Segar Pilihan", style = MaterialTheme.typography.headlineSmall, color = SavoraOnSurface)
                        TextButton(onClick = onNavigateToStore) {
                            Text("Belanja", color = SavoraPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.products.take(6).chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            pair.forEach { product ->
                                ProductCard(
                                    product = product,
                                    modifier = Modifier.weight(1f),
                                    onAddToCart = { cartViewModel.addToCart(product.id) },
                                )
                            }
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
