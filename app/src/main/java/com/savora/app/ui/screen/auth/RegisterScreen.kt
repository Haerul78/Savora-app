package com.savora.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onRegisterSuccess()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearError()
        }
    }

    // Password strength
    val passwordStrength = when {
        password.length < 4 -> 0
        password.length < 8 -> 1
        password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 3
        password.length >= 8 -> 2
        else -> 1
    }
    val strengthLabel = when (passwordStrength) {
        0 -> ""
        1 -> "Lemah"
        2 -> "Sedang"
        3 -> "Kuat"
        else -> ""
    }
    val strengthColor = when (passwordStrength) {
        1 -> PasswordWeak
        2 -> PasswordMedium
        3 -> PasswordStrong
        else -> Color.Transparent
    }

    val isFormValid = fullName.isNotBlank() && email.contains("@") &&
            phone.isNotBlank() && password.length >= 8 &&
            password == confirmPassword && agreedToTerms

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SavoraTertiary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = SavoraSurface
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SavoraSurface)
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Restaurant,
                contentDescription = null,
                tint = SavoraPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Savora",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = SavoraPrimary
            )
        }
        Text(
            text = "Masak Lezat, Belanja Sehat Setiap Hari.",
            style = MaterialTheme.typography.bodySmall,
            color = SavoraOnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // Nama Lengkap
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nama Lengkap") },
            leadingIcon = { Icon(Icons.Filled.Person, null, tint = SavoraOnSurfaceVariant) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SavoraPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = SavoraSurfaceContainerHigh,
                unfocusedContainerColor = SavoraSurfaceContainerHigh,
                cursorColor = SavoraPrimary
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = SavoraOnSurfaceVariant) },
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

        Spacer(modifier = Modifier.height(12.dp))

        // Nomor Telepon
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Nomor Telepon") },
            leadingIcon = { Icon(Icons.Filled.Call, null, tint = SavoraOnSurfaceVariant) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SavoraPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = SavoraSurfaceContainerHigh,
                unfocusedContainerColor = SavoraSurfaceContainerHigh,
                cursorColor = SavoraPrimary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Kata Sandi
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = SavoraOnSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null, tint = SavoraOnSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

        // Password strength indicator
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { passwordStrength / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = strengthColor,
                trackColor = SavoraSurfaceContainerHigh,
            )
            Text(
                text = "Kekuatan Sandi: $strengthLabel",
                style = MaterialTheme.typography.labelSmall,
                color = strengthColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Konfirmasi Kata Sandi
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Kata Sandi") },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = SavoraOnSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null, tint = SavoraOnSurfaceVariant
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPassword.isNotEmpty() && password != confirmPassword,
            supportingText = if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                { Text("Kata sandi tidak cocok", color = SavoraTertiary) }
            } else null,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Terms checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { agreedToTerms = !agreedToTerms }
        ) {
            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = SavoraPrimary)
            )
            Text(
                text = "Saya setuju dengan Syarat & Ketentuan serta Kebijakan Privasi",
                style = MaterialTheme.typography.bodySmall,
                color = SavoraOnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Daftar
        Button(
            onClick = {
                authViewModel.signUp(email, password, fullName, phone, onRegisterSuccess)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            enabled = isFormValid && !uiState.isLoading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (isFormValid) Brush.linearGradient(
                            listOf(SavoraPrimary, SavoraPrimaryContainer)
                        ) else Brush.linearGradient(
                            listOf(SavoraOnSurfaceVariant.copy(alpha = 0.3f), SavoraOnSurfaceVariant.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Daftar Akun", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = SavoraOutlineVariant.copy(alpha = 0.3f))
            Text("  atau daftar dengan  ", style = MaterialTheme.typography.labelMedium, color = SavoraOnSurfaceVariant)
            HorizontalDivider(modifier = Modifier.weight(1f), color = SavoraOutlineVariant.copy(alpha = 0.3f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google
        OutlinedButton(
            onClick = { authViewModel.signInWithGoogle() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SavoraSurfaceContainerLowest),
            border = null
        ) {
            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Google", style = MaterialTheme.typography.titleSmall, color = SavoraOnSurface)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Sudah punya akun? ", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = SavoraPrimary,
                modifier = Modifier.clickable { onNavigateBack() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
    } // end Scaffold
}
