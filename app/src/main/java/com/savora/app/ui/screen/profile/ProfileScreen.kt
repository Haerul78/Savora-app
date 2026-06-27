package com.savora.app.ui.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.savora.app.navigation.BottomNavBar
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToPaymentHistory: () -> Unit = {},
    onNavigateToSavedRecipes: () -> Unit = {},
    onNavigateToAddresses: () -> Unit = {},
    onLogout: () -> Unit = {},
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = SavoraSurface
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // App bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Savora", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SavoraPrimary)
                Spacer(Modifier.weight(1f))
            }

            // Profile header card
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = SavoraPrimaryContainer
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(72.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            val initials = user?.fullName?.split(" ")?.take(2)?.map { it.firstOrNull() ?: ' ' }?.joinToString("") ?: "?"
                            Text(initials, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(user?.fullName ?: "Pengguna", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(user?.email ?: "", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(16.dp))
                    // Stats
                    Row {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.completedOrders}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                            Text("Pesanan Selesai", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.8f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Menu list
            Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), color = SavoraSurfaceContainerLowest) {
                Column {
                    ProfileMenuItem(Icons.Filled.ReceiptLong, "Riwayat Pesanan", onClick = onNavigateToPaymentHistory)
                    ProfileMenuItem(Icons.Filled.Bookmark, "Resep Tersimpan", onClick = onNavigateToSavedRecipes)
                    ProfileMenuItem(Icons.Filled.LocationOn, "Alamat Saya", onClick = onNavigateToAddresses)
                    ProfileMenuItem(Icons.Filled.Settings, "Pengaturan", onClick = { })
                }
            }

            Spacer(Modifier.height(24.dp))

            // Logout
            TextButton(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Logout, null, tint = SavoraTertiary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar", color = SavoraTertiary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}
