package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Recipe
import com.savora.app.data.repository.RecipeRepository
import com.savora.app.data.repository.SavedRecipeRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val allRecipes: List<Recipe> = emptyList(),
    val displayedRecipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val savedRecipeIds: Set<String> = emptySet(),
    val error: String? = null,
)

class RecipeListViewModel : ViewModel() {
    private val recipeRepo = RecipeRepository()
    private val savedRepo = SavedRecipeRepository()

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState

    val categories = listOf("Semua", "Padang", "Jawa", "Sunda", "Betawi", "Sulawesi", "Lainnya")

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userId = supabase.auth.currentUserOrNull()?.id
            val recipesResult = recipeRepo.getRecipes()
            val savedIds = if (userId != null)
                savedRepo.getSavedRecipes(userId).getOrDefault(emptyList()).map { it.recipeId }.toSet()
            else emptySet()

            val recipes = recipesResult.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allRecipes = recipes,
                displayedRecipes = recipes,
                savedRecipeIds = savedIds,
                error = recipesResult.exceptionOrNull()?.message
            )
        }
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilter()
    }

    fun filterByCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilter()
    }

    private fun applyFilter() {
        val all = _uiState.value.allRecipes
        val query = _uiState.value.searchQuery.trim().lowercase()
        val category = _uiState.value.selectedCategory
        val filtered = all.filter { recipe ->
            (category == "Semua" || recipe.category == category) &&
            (query.isBlank() || recipe.title.lowercase().contains(query))
        }
        _uiState.value = _uiState.value.copy(displayedRecipes = filtered)
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
}
