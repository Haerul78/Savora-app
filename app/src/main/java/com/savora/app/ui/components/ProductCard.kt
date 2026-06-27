package com.savora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.savora.app.data.model.Product
import com.savora.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit = {},
) {
    val context = LocalContext.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Surface(
        modifier = modifier.shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
            ambientColor = SavoraOnSurface.copy(alpha = 0.06f),
            spotColor = SavoraOnSurface.copy(alpha = 0.06f),
        ),
        shape = RoundedCornerShape(16.dp),
        color = SavoraSurfaceContainerLowest,
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl?.replace(" ", "%20"))
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SavoraSurfaceContainerHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = SavoraPrimary,
                                strokeWidth = 2.dp,
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SavoraSurfaceContainerHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.ShoppingBasket,
                                contentDescription = null,
                                tint = SavoraOnSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    },
                )
                FloatingActionButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp),
                    shape = CircleShape,
                    containerColor = SavoraPrimary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                ) {
                    Icon(Icons.Filled.AddShoppingCart, null, modifier = Modifier.size(18.dp))
                }
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = SavoraOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    formatter.format(product.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SavoraPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
