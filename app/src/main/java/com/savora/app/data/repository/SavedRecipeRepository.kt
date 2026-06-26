package com.savora.app.data.repository

import com.savora.app.data.model.SavedRecipe
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from

class SavedRecipeRepository {

    suspend fun getSavedRecipes(userId: String): Result<List<SavedRecipe>> {
        return try {
            val result = supabase.from("saved_recipes").select { filter { eq("user_id", userId) } }.decodeList<SavedRecipe>()
            Result.success(result.sortedByDescending { it.savedAt })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun saveRecipe(userId: String, recipeId: String): Result<Unit> {
        return try {
            supabase.from("saved_recipes").insert(mapOf("user_id" to userId, "recipe_id" to recipeId))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun unsaveRecipe(userId: String, recipeId: String): Result<Unit> {
        return try {
            supabase.from("saved_recipes").delete { filter { eq("user_id", userId); eq("recipe_id", recipeId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun isRecipeSaved(userId: String, recipeId: String): Boolean {
        return try {
            val result = supabase.from("saved_recipes").select {
                filter { eq("user_id", userId); eq("recipe_id", recipeId) }
            }.decodeList<SavedRecipe>()
            result.isNotEmpty()
        } catch (e: Exception) { false }
    }
}