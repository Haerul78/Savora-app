package com.savora.app.data.repository

import com.savora.app.data.model.Product
import com.savora.app.data.model.ProductCategory
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from

class ProductRepository {

    suspend fun getProducts(categoryId: String? = null): Result<List<Product>> {
        return try {
            val result = supabase.from("products").select {
                filter { eq("is_available", true); if (categoryId != null) eq("category_id", categoryId) }
            }.decodeList<Product>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val result = supabase.from("products").select { filter { eq("id", productId) } }.decodeSingle<Product>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getCategories(): Result<List<ProductCategory>> {
        return try {
            val result = supabase.from("product_categories").select().decodeList<ProductCategory>()
            Result.success(result.sortedBy { it.sortOrder })
        } catch (e: Exception) { Result.failure(e) }
    }
}