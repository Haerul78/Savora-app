package com.savora.app.data.repository

import com.savora.app.data.model.Address
import com.savora.app.data.model.User
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserRepository {

    suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val result = supabase.from("users").select { filter { eq("id", userId) } }.decodeSingle<User>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateProfile(userId: String, updates: Map<String, String>): Result<Unit> {
        return try {
            supabase.from("users").update(updates) { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAddresses(userId: String): Result<List<Address>> {
        return try {
            val result = supabase.from("addresses").select { filter { eq("user_id", userId) } }.decodeList<Address>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPrimaryAddress(userId: String): Result<Address?> {
        return try {
            val result = supabase.from("addresses").select {
                filter { eq("user_id", userId); eq("is_primary", true) }
            }.decodeList<Address>()
            Result.success(result.firstOrNull())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addAddress(
        userId: String, label: String, recipientName: String, phone: String,
        fullAddress: String, city: String, province: String, postalCode: String, isPrimary: Boolean,
    ): Result<Unit> {
        return try {
            supabase.from("addresses").insert(buildJsonObject {
                put("user_id", userId); put("label", label); put("recipient_name", recipientName)
                put("phone", phone); put("full_address", fullAddress); put("city", city)
                put("province", province); put("postal_code", postalCode); put("is_primary", isPrimary)
            })
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getCompletedOrdersCount(userId: String): Int {
        return try {
            val result = supabase.from("orders").select {
                filter { eq("user_id", userId); eq("status", "delivered") }
            }.decodeList<com.savora.app.data.model.Order>()
            result.size
        } catch (e: Exception) { 0 }
    }
}