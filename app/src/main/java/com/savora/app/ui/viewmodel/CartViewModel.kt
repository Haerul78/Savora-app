package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.CartItemWithDetails
import com.savora.app.data.model.Address
import com.savora.app.data.model.RecipeIngredient
import com.savora.app.data.repository.CartRepository
import com.savora.app.data.repository.ProductRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItemWithDetails> = emptyList(),
    val primaryAddress: Address? = null,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 15000.0,
    val total: Double = 0.0,
    val error: String? = null,
)

class CartViewModel : ViewModel() {
    private val cartRepo = CartRepository()
    private val productRepo = ProductRepository()
    private val userRepo = UserRepository()

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)

            val cartResult = cartRepo.getCartItems(userId)
            val addressResult = userRepo.getPrimaryAddress(userId)
            val cartItems = cartResult.getOrDefault(emptyList())

            // Fetch semua produk secara paralel, bukan berurutan
            val itemsWithDetails = coroutineScope {
                cartItems.map { cartItem ->
                    async {
                        productRepo.getProductById(cartItem.productId).getOrNull()?.let { product ->
                            CartItemWithDetails(cartItem = cartItem, product = product, recipeName = null)
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            val subtotal = itemsWithDetails.sumOf { it.product.price * it.cartItem.quantity }
            val deliveryFee = if (itemsWithDetails.isNotEmpty()) 15000.0 else 0.0

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                cartItems = itemsWithDetails,
                primaryAddress = addressResult.getOrNull(),
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = subtotal + deliveryFee,
            )
        }
    }

    fun addToCart(productId: String, recipeId: String? = null, quantity: Int = 1) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _uiState.value = _uiState.value.copy(error = "Silakan login terlebih dahulu")
                return@launch
            }
            val result = cartRepo.addToCart(userId, productId, recipeId, quantity)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = "Gagal menambahkan ke keranjang: ${result.exceptionOrNull()?.message}"
                )
            }
            loadCart()
        }
    }

    // Tambahkan semua bahan resep yang punya productId ke cart sekaligus
    // Mengembalikan true jika minimal 1 bahan berhasil ditambahkan
    suspend fun addIngredientsToCartAndWait(recipeId: String, ingredients: List<RecipeIngredient>): Boolean {
        val buyableIngredients = ingredients.filter { !it.isOptional && it.productId != null }
        if (buyableIngredients.isEmpty()) return false
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "Silakan login terlebih dahulu")
            return false
        }
        var anySuccess = false
        buyableIngredients.forEach { ingredient ->
            val result = cartRepo.addToCart(userId, ingredient.productId!!, recipeId, 1)
            if (result.isSuccess) anySuccess = true
        }
        loadCart()
        return anySuccess
    }

    fun addIngredientsToCart(recipeId: String, ingredients: List<RecipeIngredient>) {
        viewModelScope.launch {
            addIngredientsToCartAndWait(recipeId, ingredients)
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepo.updateQuantity(cartItemId, newQuantity)
            loadCart()
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            cartRepo.removeItem(cartItemId)
            loadCart()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            cartRepo.clearCart(userId)
            loadCart()
        }
    }
}
