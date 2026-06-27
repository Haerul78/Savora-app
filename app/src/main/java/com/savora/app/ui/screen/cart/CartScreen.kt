package com.savora.app.ui.screen.cart

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit = {},
    onCheckout: () -> Unit = {},
    viewModel: CartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    // Reload cart setiap kali CartScreen ditampilkan
    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    Scaffold(
        containerColor = SavoraSurface,
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Belanja", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(Icons.Filled.DeleteSweep, "Hapus Semua", tint = SavoraOnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SavoraSurface)
            )
        },
        bottomBar = {
            // Order Summary
            Surface(
                color = SavoraSurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Address
                    if (uiState.primaryAddress != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, null, tint = SavoraPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(uiState.primaryAddress!!.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                Text(uiState.primaryAddress!!.fullAddress, style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        TextButton(onClick = { }) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Alamat")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                        Text(formatter.format(uiState.subtotal), style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Pengiriman", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                        Text(formatter.format(uiState.deliveryFee), style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(formatter.format(uiState.total), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SavoraPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = uiState.cartItems.isNotEmpty() && uiState.primaryAddress != null
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (uiState.cartItems.isNotEmpty() && uiState.primaryAddress != null)
                                        Brush.linearGradient(listOf(SavoraPrimary, SavoraPrimaryContainer))
                                    else Brush.linearGradient(listOf(SavoraOnSurfaceVariant.copy(0.3f), SavoraOnSurfaceVariant.copy(0.3f))),
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Lanjut ke Pembayaran", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SavoraPrimary)
            }
        } else if (uiState.cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ShoppingCart, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Keranjang masih kosong", style = MaterialTheme.typography.titleSmall, color = SavoraOnSurfaceVariant)
                    Text("Yuk, belanja bahan masakan!", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                // Group by recipe
                val withRecipe = uiState.cartItems.filter { it.cartItem.recipeId != null }
                val withoutRecipe = uiState.cartItems.filter { it.cartItem.recipeId == null }

                if (withRecipe.isNotEmpty()) {
                    Surface(color = SavoraSurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.RestaurantMenu, null, tint = SavoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dari Resep", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.weight(1f))
                            Badge(containerColor = SavoraPrimary) { Text("${withRecipe.size}", color = Color.White) }
                        }
                    }
                    withRecipe.forEach { item -> CartItemRow(item, formatter, viewModel) }
                }

                if (withoutRecipe.isNotEmpty()) {
                    Surface(color = SavoraSurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ShoppingBasket, null, tint = SavoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kebutuhan Dapur", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.weight(1f))
                            Badge(containerColor = SavoraPrimary) { Text("${withoutRecipe.size}", color = Color.White) }
                        }
                    }
                    withoutRecipe.forEach { item -> CartItemRow(item, formatter, viewModel) }
                }

                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: com.savora.app.data.model.CartItemWithDetails,
    formatter: NumberFormat,
    viewModel: CartViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${item.cartItem.quantity} ${item.product.unit}", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
            Text(formatter.format(item.product.price), style = MaterialTheme.typography.bodyMedium, color = SavoraPrimary, fontWeight = FontWeight.SemiBold)
        }
        // Quantity controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { viewModel.updateQuantity(item.cartItem.id, item.cartItem.quantity - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Filled.Remove, null, tint = SavoraPrimary, modifier = Modifier.size(18.dp))
            }
            Text("${item.cartItem.quantity}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(
                onClick = { viewModel.updateQuantity(item.cartItem.id, item.cartItem.quantity + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Filled.Add, null, tint = SavoraPrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
