package com.savora.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savora.app.ui.theme.*

@Composable
fun SavoraTopBar(
    cityLabel: String = "Tentukan alamat",
    cartCount: Int = 0,
    onCartClick: () -> Unit = {},
) {
    Surface(
        color = SavoraSurface.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.LocationOn, null,
                tint = SavoraPrimary,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.padding(start = 4.dp).weight(1f)) {
                Text(
                    "Kirim ke:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SavoraOnSurfaceVariant
                )
                Text(
                    cityLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SavoraOnSurface
                )
            }
            Text(
                "Savora",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = SavoraPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = SavoraTertiary) {
                                Text("$cartCount", color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.ShoppingCart, "Keranjang", tint = SavoraOnSurface)
                }
            }
        }
    }
}
