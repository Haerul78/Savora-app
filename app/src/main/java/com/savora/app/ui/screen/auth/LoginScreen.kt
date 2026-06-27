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
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SavoraSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero Section ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .heightIn(min = 280.dp)
                .background(SavoraPrimary)
        ) {
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

        // ── Card ─────────────────────────────────────────────────
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
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SavoraOnSurface,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

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

                // Kata Sandi
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

                Spacer(modifier = Modifier.height(16.dp))

                // Link Daftar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Belum punya akun? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = SavoraOnSurfaceVariant
                    )
                    Text(
                        text = "Daftar",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = SavoraPrimary,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
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

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
