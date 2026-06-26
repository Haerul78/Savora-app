package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.Recipe
import com.savora.app.data.model.RecipeIngredient
import com.savora.app.data.model.RecipeStep
import com.savora.app.data.repository.RecipeRepository
import com.savora.app.data.repository.SavedRecipeRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val ingredients: List<RecipeIngredient> = emptyList(),
    val steps: List<RecipeStep> = emptyList(),
    val isSaved: Boolean = false,
    val savedMessage: String? = null,
    val error: String? = null,
)

class RecipeDetailViewModel : ViewModel() {
    private val recipeRepo = RecipeRepository()
    private val savedRepo = SavedRecipeRepository()

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val userId = supabase.auth.currentUserOrNull()?.id
            coroutineScope {
                val recipeDeferred = async { recipeRepo.getRecipeById(recipeId) }
                val ingredientsDeferred = async { recipeRepo.getRecipeIngredients(recipeId) }
                val stepsDeferred = async { recipeRepo.getRecipeSteps(recipeId) }
                val savedDeferred = async {
                    if (userId != null) savedRepo.isRecipeSaved(userId, recipeId) else false
                }

                val recipeResult = recipeDeferred.await()
                val ingredientsResult = ingredientsDeferred.await()
                val stepsResult = stepsDeferred.await()
                val isSaved = savedDeferred.await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recipe = recipeResult.getOrNull(),
                    ingredients = ingredientsResult.getOrDefault(emptyList()),
                    steps = stepsResult.getOrDefault(emptyList()),
                    isSaved = isSaved,
                    error = recipeResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun toggleSaveRecipe(recipeId: String) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            if (_uiState.value.isSaved) {
                val result = savedRepo.unsaveRecipe(userId, recipeId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isSaved = false, savedMessage = "Resep dihapus dari simpanan")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Gagal menghapus simpanan")
                }
            } else {
                val result = savedRepo.saveRecipe(userId, recipeId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isSaved = true, savedMessage = "Resep berhasil disimpan")
                } else {
                    _uiState.value = _uiState.value.copy(error = "Gagal menyimpan resep")
                }
            }
        }
    }

    fun clearSavedMessage() {
        _uiState.value = _uiState.value.copy(savedMessage = null)
    }
}
