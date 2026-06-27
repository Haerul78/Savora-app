package com.savora.app.data.repository

import com.savora.app.data.model.CartItem
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CartRepository {

    suspend fun getCartItems(userId: String): Result<List<CartItem>> {
        return try {
            val result = supabase.from("cart_items").select { filter { eq("user_id", userId) } }.decodeList<CartItem>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addToCart(userId: String, productId: String, recipeId: String? = null, quantity: Int = 1): Result<Unit> {
        return try {
            val existing = supabase.from("cart_items").select {
                filter { eq("user_id", userId); eq("product_id", productId) }
            }.decodeList<CartItem>()
            if (existing.isNotEmpty()) {
                supabase.from("cart_items").update(buildJsonObject { put("quantity", existing[0].quantity + quantity) }) {
                    filter { eq("id", existing[0].id) }
                }
            } else {
                supabase.from("cart_items").insert(buildJsonObject {
                    put("user_id", userId); put("product_id", productId); put("quantity", quantity)
                    if (recipeId != null) put("recipe_id", recipeId)
                })
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                supabase.from("cart_items").delete { filter { eq("id", cartItemId) } }
            } else {
                supabase.from("cart_items").update(buildJsonObject { put("quantity", quantity) }) {
                    filter { eq("id", cartItemId) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun removeItem(cartItemId: String): Result<Unit> {
        return try { supabase.from("cart_items").delete { filter { eq("id", cartItemId) } }; Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun clearCart(userId: String): Result<Unit> {
        return try { supabase.from("cart_items").delete { filter { eq("user_id", userId) } }; Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }
}