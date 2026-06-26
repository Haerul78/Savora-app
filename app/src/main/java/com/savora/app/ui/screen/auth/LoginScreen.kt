package com.savora.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val uiState by authViewModel.uiState.collectAsState()

    // Navigate to Home setelah Google OAuth callback berhasil
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SavoraSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero Section (60% tinggi layar) ─────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .heightIn(min = 280.dp)
                .background(SavoraPrimary)
        ) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SavoraPrimary,
                                SavoraPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Logo & Tagline
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Restaurant,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Savora",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cita Rasa Nusantara di Tanganmu.",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // ── Bottom Sheet Card ───────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = SavoraSurface,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                // ── Tabs: Masuk | Daftar ─────────────────────────
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Masuk", "Daftar").forEachIndexed { index, label ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) SavoraPrimary else SavoraOnSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(
                                        if (selectedTab == index) SavoraPrimary
                                        else Color.Transparent,
                                        RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (selectedTab == 0) {
                    // ── Tab Masuk ──────────────────────────────────
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email") },
                        placeholder = { Text("Masukkan email kamu") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = SavoraOnSurfaceVariant)
                        },
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it, color = SavoraTertiary) } },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SavoraPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = SavoraSurfaceContainerHigh,
                            unfocusedContainerColor = SavoraSurfaceContainerHigh,
                            cursorColor = SavoraPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Kata Sandi") },
                        placeholder = { Text("Masukkan kata sandi") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = SavoraOnSurfaceVariant)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                    tint = SavoraOnSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it, color = SavoraTertiary) } },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SavoraPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = SavoraSurfaceContainerHigh,
                            unfocusedContainerColor = SavoraSurfaceContainerHigh,
                            cursorColor = SavoraPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Lupa Kata Sandi
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Lupa kata sandi?",
                            style = MaterialTheme.typography.bodySmall,
                            color = SavoraPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(top = 8.dp)
                                .clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = SavoraTertiary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Tombol Masuk
                    Button(
                        onClick = {
                            emailError = if (email.isBlank() || !email.contains("@")) "Email tidak valid" else null
                            passwordError = if (password.length < 8) "Minimal 8 karakter" else null
                            if (emailError == null && passwordError == null) {
                                authViewModel.signIn(email, password, onLoginSuccess)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = !uiState.isLoading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(SavoraPrimary, SavoraPrimaryContainer)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Masuk",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = SavoraOutlineVariant.copy(alpha = 0.3f))
                        Text(
                            text = "  Atau masuk dengan  ",
                            style = MaterialTheme.typography.labelMedium,
                            color = SavoraOnSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = SavoraOutlineVariant.copy(alpha = 0.3f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google button
                    OutlinedButton(
                        onClick = { authViewModel.signInWithGoogle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SavoraSurfaceContainerLowest
                        ),
                        border = null
                    ) {
                        Text(
                            text = "G",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Google",
                            style = MaterialTheme.typography.titleSmall,
                            color = SavoraOnSurface
                        )
                    }
                } else {
                    // ── Tab Daftar ─────────────────────────────────
                    Text(
                        text = "Buat akun baru dan mulai jelajahi resep lezat Nusantara!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SavoraOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )

                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(SavoraPrimary, SavoraPrimaryContainer)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Daftar Gratis",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
