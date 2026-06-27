package com.savora.app.data.repository

import com.savora.app.data.model.Order
import com.savora.app.data.model.OrderItem
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class OrderRepository {

    suspend fun createOrder(order: JsonObject): Result<Order> {
        return try {
            val result = supabase.from("orders").insert(order) { select() }.decodeSingle<Order>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createOrderItems(items: List<JsonObject>): Result<Unit> {
        return try { supabase.from("order_items").insert(items); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val result = supabase.from("orders").select { filter { eq("id", orderId) } }.decodeSingle<Order>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val result = supabase.from("orders").select { filter { eq("user_id", userId) } }.decodeList<Order>()
            Result.success(result.sortedByDescending { it.createdAt })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getOrderItems(orderId: String): Result<List<OrderItem>> {
        return try {
            val result = supabase.from("order_items").select { filter { eq("order_id", orderId) } }.decodeList<OrderItem>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            supabase.from("orders").update(buildJsonObject { put("status", status) }) { filter { eq("id", orderId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}