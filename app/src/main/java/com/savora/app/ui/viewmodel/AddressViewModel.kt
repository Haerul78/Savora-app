package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Address
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddressUiState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null,
)

class AddressViewModel : ViewModel() {
    private val userRepo = UserRepository()

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = userRepo.getAddresses(userId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                addresses = result.getOrDefault(emptyList())
            )
        }
    }

    fun addAddress(
        label: String,
        recipientName: String,
        phone: String,
        fullAddress: String,
        city: String,
        province: String,
        postalCode: String,
        isPrimary: Boolean,
    ) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = userRepo.addAddress(userId, label, recipientName, phone, fullAddress, city, province, postalCode, isPrimary)
            if (result.isSuccess) {
                loadAddresses()
                _uiState.value = _uiState.value.copy(successMessage = "Alamat berhasil ditambahkan")
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Gagal menambahkan alamat"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }
}
