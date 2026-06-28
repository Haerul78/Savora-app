package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.repository.AuthRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    val successMessage: String? = null,
    val error: String? = null,
)

class SettingsViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            userRepo.getUserProfile(userId).onSuccess { user ->
                _uiState.value = _uiState.value.copy(fullName = user.fullName, email = user.email ?: "")
            }
        }
    }

    fun changePassword(newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (newPassword.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Kata sandi minimal 6 karakter.")
            return
        }
        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Konfirmasi kata sandi tidak cocok.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.changePassword(newPassword).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Kata sandi berhasil diubah.")
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Gagal mengubah kata sandi. Silakan coba lagi.")
                }
            )
        }
    }

    fun updateName(newName: String, onSuccess: () -> Unit) {
        if (newName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Nama tidak boleh kosong.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            userRepo.updateProfile(userId, mapOf("full_name" to newName)).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, fullName = newName, successMessage = "Profil berhasil diperbarui.")
                    onSuccess()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Gagal memperbarui profil. Silakan coba lagi.")
                }
            )
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.deleteAccount().fold(
                onSuccess = { onSuccess() },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Gagal menghapus akun. Silakan coba lagi.")
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
