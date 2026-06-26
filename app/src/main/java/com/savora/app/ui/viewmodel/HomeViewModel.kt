package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Address
import com.savora.app.data.model.Product
import com.savora.app.data.model.Recipe
import com.savora.app.data.repository.ProductRepository
import com.savora.app.data.repository.RecipeRepository
import com.savora.app.data.repository.SavedRecipeRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val allRecipes: List<Recipe> = emptyList(),
    val displayedRecipes: List<Recipe> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategory: String = "Semua",
    val searchQuery: String = "",
    val primaryAddress: Address? = null,
    val savedRecipeIds: Set<String> = emptySet(),
    val error: String? = null,
)

class HomeViewModel : ViewModel() {
    private val recipeRepo = RecipeRepository()
    private val productRepo = ProductRepository()
    private val savedRepo = SavedRecipeRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    val categories = listOf("Semua", "Padang", "Jawa", "Sunda", "Betawi", "Sulawesi")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            coroutineScope {
                val userId = supabase.auth.currentUserOrNull()?.id
                val recipesDeferred = async { recipeRepo.getRecipes() }
                val productsDeferred = async { productRepo.getProducts() }
                val addressDeferred = async {
                    if (userId != null) UserRepository().getPrimaryAddress(userId).getOrNull() else null
                }
                val savedDeferred = async {
                    if (userId != null)
                        savedRepo.getSavedRecipes(userId).getOrDefault(emptyList()).map { it.recipeId }.toSet()
                    else emptySet()
                }
                val recipesResult = recipesDeferred.await()
                val productsResult = productsDeferred.await()
                val address = addressDeferred.await()
                val savedIds = savedDeferred.await()
                val recipes = recipesResult.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allRecipes = recipes,
                    displayedRecipes = applyRecipeFilter(recipes, _uiState.value.selectedCategory, _uiState.value.searchQuery),
                    products = productsResult.getOrDefault(emptyList()),
                    primaryAddress = address,
                    savedRecipeIds = savedIds,
                    error = recipesResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun toggleSaveRecipe(recipeId: String) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val isSaved = _uiState.value.savedRecipeIds.contains(recipeId)
            if (isSaved) {
                savedRepo.unsaveRecipe(userId, recipeId)
                _uiState.value = _uiState.value.copy(
                    savedRecipeIds = _uiState.value.savedRecipeIds - recipeId
                )
            } else {
                savedRepo.saveRecipe(userId, recipeId)
                _uiState.value = _uiState.value.copy(
                    savedRecipeIds = _uiState.value.savedRecipeIds + recipeId
                )
            }
        }
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            displayedRecipes = applyRecipeFilter(_uiState.value.allRecipes, _uiState.value.selectedCategory, query)
        )
    }

    fun filterByCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            displayedRecipes = applyRecipeFilter(_uiState.value.allRecipes, category, _uiState.value.searchQuery)
        )
    }

    private fun applyRecipeFilter(recipes: List<Recipe>, category: String, query: String): List<Recipe> {
        val q = query.trim().lowercase()
        return recipes.filter { recipe ->
            (category == "Semua" || recipe.category == category) &&
                    (q.isBlank() || recipe.title.lowercase().contains(q))
        }
    }
}
