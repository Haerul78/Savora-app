package com.savora.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.savora.app.data.model.Recipe
import com.savora.app.ui.theme.*

@Composable
fun RecipeCard(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    showCookTime: Boolean = false,
    onClick: () -> Unit,
    onToggleSave: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = SavoraOnSurface.copy(alpha = 0.06f),
                spotColor = SavoraOnSurface.copy(alpha = 0.06f),
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SavoraSurfaceContainerLowest,
    ) {
        Column {
            Box {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                )
                // Rating badge
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.6f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Star, null, tint = SavoraStar, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${recipe.rating}", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
                // Bookmark
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp),
                ) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Hapus simpanan" else "Simpan resep",
                        tint = if (isSaved) SavoraPrimary else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = SavoraOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    recipe.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = SavoraOnSurfaceVariant,
                )
                if (showCookTime) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule, null,
                            tint = SavoraOnSurfaceVariant,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            " ${recipe.cookTimeMinutes} menit",
                            style = MaterialTheme.typography.labelSmall,
                            color = SavoraOnSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
