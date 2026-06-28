package com.savora.app.ui.screen.profile

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = uiState.fullName,
            isLoading = uiState.isLoading,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { newName ->
                viewModel.updateName(newName) { showEditProfileDialog = false }
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isLoading = uiState.isLoading,
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { newPass, confirmPass ->
                viewModel.changePassword(newPass, confirmPass) {
                    showChangePasswordDialog = false
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Warning, null, tint = SavoraTertiary) },
            title = { Text("Hapus Akun", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Akun dan semua data kamu akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SavoraOnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteAccount { showDeleteDialog = false; onAccountDeleted() } },
                    colors = ButtonDefaults.buttonColors(containerColor = SavoraTertiary),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Hapus Akun")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
            containerColor = SavoraSurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SavoraOnSurface,
                    contentColor = SavoraSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = SavoraSurface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, "Kembali", tint = SavoraOnSurface)
                }
                Text(
                    "Pengaturan",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SavoraOnSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Akun ─────────────────────────────────────────────
            SectionLabel("Akun")

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = SavoraSurfaceContainerLowest
            ) {
                Column {
                    // Avatar + Edit Profil
                    SettingsItem(
                        icon = Icons.Filled.Person,
                        label = uiState.fullName.ifEmpty { "Pengguna" },
                        subtitle = "Nama Lengkap",
                        trailingLabel = "Edit",
                        onClick = { showEditProfileDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = SavoraSurfaceContainerLow)

                    // Email (read-only)
                    SettingsInfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = uiState.email.ifEmpty { "-" }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = SavoraSurfaceContainerLow)

                    // Ganti Password
                    SettingsItem(
                        icon = Icons.Filled.Lock,
                        label = "Ganti Kata Sandi",
                        onClick = { showChangePasswordDialog = true }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Tentang Aplikasi ──────────────────────────────────
            SectionLabel("Tentang Aplikasi")

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = SavoraSurfaceContainerLowest
            ) {
                Column {
                    SettingsInfoItem(label = "Nama Aplikasi", value = "Savora")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = SavoraSurfaceContainerLow)
                    SettingsInfoItem(label = "Versi", value = "1.0.0")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = SavoraSurfaceContainerLow)
                    SettingsInfoItem(label = "Dikembangkan oleh", value = "Halva Studio")
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Hapus Akun ────────────────────────────────────────
            SectionLabel("Zona Berbahaya")

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = SavoraTertiary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hapus Akun", style = MaterialTheme.typography.titleSmall, color = SavoraTertiary, fontWeight = FontWeight.SemiBold)
                        Text("Semua data akan dihapus permanen", style = MaterialTheme.typography.labelSmall, color = SavoraTertiary.copy(alpha = 0.7f))
                    }
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SavoraTertiary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hapus", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = SavoraOnSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String? = null,
    trailingLabel: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant)
            }
            Text(label, style = MaterialTheme.typography.bodyMedium, color = SavoraOnSurface)
        }
        if (trailingLabel != null) {
            Text(trailingLabel, style = MaterialTheme.typography.labelMedium, color = SavoraPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(4.dp))
        }
        Icon(Icons.Filled.ChevronRight, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = SavoraOnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = SavoraOnSurface)
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Person, null, modifier = Modifier.size(20.dp)) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = !isLoading && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SavoraPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        containerColor = SavoraSurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ChangePasswordDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ganti Kata Sandi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Kata Sandi Baru") },
                    singleLine = true,
                    visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNew = !showNew }) {
                            Icon(if (showNew) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Kata Sandi") },
                    singleLine = true,
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirm = !showConfirm }) {
                            Icon(if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newPassword, confirmPassword) },
                enabled = !isLoading && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SavoraPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        containerColor = SavoraSurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp)
    )
}
