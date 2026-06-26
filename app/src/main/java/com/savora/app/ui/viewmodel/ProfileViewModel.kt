package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.User
import com.savora.app.data.repository.AuthRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val completedOrders: Int = 0,
    val error: String? = null,
)

class ProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)

            val userResult = userRepo.getUserProfile(userId)
            val ordersCount = userRepo.getCompletedOrdersCount(userId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                user = userResult.getOrNull(),
                completedOrders = ordersCount,
                error = userResult.exceptionOrNull()?.message
            )
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepo.signOut()
            onSuccess()
        }
    }
}
