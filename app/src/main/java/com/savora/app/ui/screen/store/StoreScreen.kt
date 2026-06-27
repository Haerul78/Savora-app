package com.savora.app.ui.screen.store

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.savora.app.navigation.BottomNavBar
import com.savora.app.ui.components.ProductCard
import com.savora.app.ui.components.SavoraTopBar
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.CartViewModel
import com.savora.app.ui.viewmodel.StoreViewModel

@Composable
fun StoreScreen(
    onCartClick: () -> Unit = {},
    navController: NavController,
    viewModel: StoreViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartCount = cartUiState.cartItems.sumOf { it.cartItem.quantity }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadData() }

    LaunchedEffect(cartUiState.error) {
        cartUiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                cartCount = cartCount,
                onCartClick = onCartClick,
            )

            // Search
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SavoraSurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Search, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cari bahan masakan...", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val tabs = listOf("Semua" to null) + uiState.categories.map { it.name to it.id }
                tabs.forEach { (name, id) ->
                    val isSelected = uiState.selectedCategoryId == id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterByCategory(id) },
                        label = { Text(name) },
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Produk Segar",
                style = MaterialTheme.typography.headlineSmall,
                color = SavoraOnSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = SavoraPrimary)
                }
            } else {
                uiState.products.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
