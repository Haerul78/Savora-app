package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Address
import com.savora.app.data.model.Product
import com.savora.app.data.model.ProductCategory
import com.savora.app.data.repository.ProductRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StoreUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<ProductCategory> = emptyList(),
    val selectedCategoryId: String? = null,
    val primaryAddress: Address? = null,
    val error: String? = null,
)

class StoreViewModel : ViewModel() {
    private val repo = ProductRepository()
    private val userRepo = UserRepository()

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            coroutineScope {
                val userId = supabase.auth.currentUserOrNull()?.id
                val productsDeferred = async { repo.getProducts() }
                val categoriesDeferred = async { repo.getCategories() }
                val addressDeferred = async {
                    if (userId != null) userRepo.getPrimaryAddress(userId).getOrNull() else null
                }
                val productsResult = productsDeferred.await()
                val categoriesResult = categoriesDeferred.await()
                val address = addressDeferred.await()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = productsResult.getOrDefault(emptyList()),
                    categories = categoriesResult.getOrDefault(emptyList()),
                    primaryAddress = address,
                    error = productsResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun filterByCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repo.getProducts(categoryId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                products = result.getOrDefault(emptyList())
            )
        }
    }
}
