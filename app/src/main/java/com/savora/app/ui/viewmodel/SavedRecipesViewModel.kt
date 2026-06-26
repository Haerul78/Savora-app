package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Recipe
import com.savora.app.data.repository.RecipeRepository
import com.savora.app.data.repository.SavedRecipeRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SavedRecipesUiState(
    val isLoading: Boolean = false,
    val savedRecipes: List<Recipe> = emptyList(),
    val selectedCategory: String = "Semua",
    val totalCount: Int = 0,
    val error: String? = null,
)

class SavedRecipesViewModel : ViewModel() {
    private val savedRepo = SavedRecipeRepository()
    private val recipeRepo = RecipeRepository()

    private val _uiState = MutableStateFlow(SavedRecipesUiState())
    val uiState: StateFlow<SavedRecipesUiState> = _uiState

    val categories = listOf("Semua", "Padang", "Jawa", "Sunda", "Betawi", "Sulawesi", "Lainnya")

    init {
        loadSavedRecipes()
    }

    fun loadSavedRecipes(category: String = _uiState.value.selectedCategory) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, selectedCategory = category)

            val savedResult = savedRepo.getSavedRecipes(userId)
            val savedRecipes = savedResult.getOrDefault(emptyList())

            val recipes = coroutineScope {
                savedRecipes.map { saved ->
                    async { recipeRepo.getRecipeById(saved.recipeId).getOrNull() }
                }.awaitAll().filterNotNull()
            }

            val filtered = if (category == "Semua") recipes
            else recipes.filter { it.category == category }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                savedRecipes = filtered,
                totalCount = recipes.size,
                error = savedResult.exceptionOrNull()?.message
            )
        }
    }

    fun filterByCategory(category: String) {
        loadSavedRecipes(category)
    }

    fun unsaveRecipe(recipeId: String) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            savedRepo.unsaveRecipe(userId, recipeId)
            loadSavedRecipes()
        }
    }
}
