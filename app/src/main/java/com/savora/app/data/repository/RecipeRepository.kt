package com.savora.app.data.repository

import com.savora.app.data.model.Recipe
import com.savora.app.data.model.RecipeIngredient
import com.savora.app.data.model.RecipeStep
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from

class RecipeRepository {

    suspend fun getRecipes(category: String? = null): Result<List<Recipe>> {
        return try {
            val result = supabase.from("recipes").select {
                filter {
                    eq("is_published", true)
                    if (category != null && category != "Semua") eq("category", category)
                }
            }.decodeList<Recipe>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getRecipeById(recipeId: String): Result<Recipe> {
        return try {
            val result = supabase.from("recipes").select { filter { eq("id", recipeId) } }.decodeSingle<Recipe>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getRecipeIngredients(recipeId: String): Result<List<RecipeIngredient>> {
        return try {
            val result = supabase.from("recipe_ingredients").select { filter { eq("recipe_id", recipeId) } }.decodeList<RecipeIngredient>()
            Result.success(result.sortedBy { it.sortOrder })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getRecipeSteps(recipeId: String): Result<List<RecipeStep>> {
        return try {
            val result = supabase.from("recipe_steps").select { filter { eq("recipe_id", recipeId) } }.decodeList<RecipeStep>()
            Result.success(result.sortedBy { it.stepNumber })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun searchRecipes(query: String): Result<List<Recipe>> {
        return try {
            val result = supabase.from("recipes").select {
                filter { eq("is_published", true); ilike("title", "%$query%") }
            }.decodeList<Recipe>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }
}