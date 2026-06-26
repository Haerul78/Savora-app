package com.savora.app.ui.screen.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.CartViewModel
import com.savora.app.ui.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onNavigateBack: () -> Unit = {},
    onBuyIngredients: () -> Unit = {},
    viewModel: RecipeDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isBuying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    LaunchedEffect(uiState.savedMessage) {
        uiState.savedMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSavedMessage()
        }
    }

    val recipe = uiState.recipe

    Scaffold(
        containerColor = SavoraSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // FAB / Bottom CTA
            Surface(
                color = SavoraSurface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (!isBuying) {
                                isBuying = true
                                scope.launch {
                                    cartViewModel.addIngredientsToCartAndWait(recipeId, uiState.ingredients)
                                    isBuying = false
                                    onBuyIngredients()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = !isBuying
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(SavoraPrimary, SavoraPrimaryContainer)),
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isBuying) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Beli Bahan Sekarang", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
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
        } else if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        tint = SavoraOnSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error ?: "Resep tidak ditemukan",
                        style = MaterialTheme.typography.titleSmall,
                        color = SavoraOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { viewModel.loadRecipe(recipeId) }) {
                        Text("Coba Lagi")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Hero Image ──────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp)
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, SavoraOnSurface.copy(alpha = 0.6f))
                                )
                            )
                    )
                    // Back button
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(SavoraOnSurface.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                    // Action buttons
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleSaveRecipe(recipeId) },
                            modifier = Modifier.background(SavoraOnSurface.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                if (uiState.isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                "Simpan", tint = if (uiState.isSaved) SavoraTertiary else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { },
                            modifier = Modifier.background(SavoraOnSurface.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Share, "Bagikan", tint = Color.White)
                        }
                    }
                    // Badges
                    Row(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(50), color = SavoraPrimaryContainer) {
                            Text("Populer", style = MaterialTheme.typography.labelSmall, color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        Surface(shape = RoundedCornerShape(50), color = SavoraTertiaryContainer) {
                            Text(recipe.difficulty, style = MaterialTheme.typography.labelSmall, color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }

                // ── Content ─────────────────────────────────────
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Text(recipe.title, style = MaterialTheme.typography.headlineSmall, color = SavoraOnSurface)
                    if (!recipe.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(recipe.description, style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                    }
                    // Rating
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = SavoraStar, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${recipe.rating}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Text(" (${recipe.totalReviews} Review)", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem(icon = Icons.Filled.Schedule, label = "Waktu", value = "${recipe.cookTimeMinutes} Min", modifier = Modifier.weight(1f))
                        StatItem(icon = Icons.Filled.Restaurant, label = "Porsi", value = "${recipe.servings} Org", modifier = Modifier.weight(1f))
                        StatItem(icon = Icons.Filled.Bolt, label = "Kalori", value = "-", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bahan-bahan
                    Text("Bahan-bahan", style = MaterialTheme.typography.titleMedium, color = SavoraOnSurface)
                    Text("${uiState.ingredients.size} item diperlukan", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.ingredients.forEach { ingredient ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = SavoraPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row {
                                    Text(ingredient.name, style = MaterialTheme.typography.bodyMedium, color = SavoraOnSurface)
                                    if (ingredient.isOptional) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(shape = RoundedCornerShape(50), color = SavoraSurfaceContainerLow) {
                                            Text("(Opsional)", style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                val qty = buildString {
                                    ingredient.quantity?.let { append(it.toString()) }
                                    ingredient.unit?.let { if (isNotEmpty()) append(" "); append(it) }
                                }
                                if (qty.isNotBlank()) {
                                    Text(qty, style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Langkah-langkah
                    Text("Langkah-langkah", style = MaterialTheme.typography.titleMedium, color = SavoraOnSurface)
                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.steps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SavoraPrimary,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text("${step.stepNumber}", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(step.instruction, style = MaterialTheme.typography.bodyMedium, color = SavoraOnSurface)
                                step.durationMinutes?.let { mins ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Timer, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("$mins menit", style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SavoraSurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = SavoraPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = SavoraOnSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant)
        }
    }
}
