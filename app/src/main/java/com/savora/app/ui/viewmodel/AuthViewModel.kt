package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.repository.AuthRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val error: String? = null,
)

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // Pantau perubahan session — termasuk callback dari Google OAuth
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val uid = status.session.user?.id
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            userId = uid
                        )
                        // Pastikan profil public.users ada (penting untuk Google OAuth)
                        if (uid != null) {
                            viewModelScope.launch { repo.ensureProfile(uid, status.session.user?.email) }
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        if (_uiState.value.isLoggedIn) {
                            _uiState.value = AuthUiState()
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repo.signIn(email, password)
            result.fold(
                onSuccess = { userId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = userId
                    )
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login gagal"
                    )
                }
            )
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repo.signInWithGoogle()
            result.fold(
                onSuccess = {
                    // Browser dibuka untuk OAuth — session akan di-set via deep link callback
                    // SessionStatus collector di atas yang akan update isLoggedIn
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Google Sign-In gagal"
                    )
                }
            )
        }
    }

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repo.signUp(email, password, fullName, phone)
            result.fold(
                onSuccess = { userId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = userId
                    )
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Registrasi gagal"
                    )
                }
            )
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repo.signOut()
            _uiState.value = AuthUiState()
            onSuccess()
        }
    }

    fun checkSession(onLoggedIn: () -> Unit, onNotLoggedIn: () -> Unit) {
        viewModelScope.launch {
            val isLoggedIn = repo.isLoggedIn()
            if (isLoggedIn) {
                val userId = repo.getCurrentSession()
                _uiState.value = _uiState.value.copy(isLoggedIn = true, userId = userId)
                onLoggedIn()
            } else {
                onNotLoggedIn()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
