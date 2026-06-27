package com.savora.app.data.repository

import com.savora.app.data.model.Address
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from

class UserRepository {
    suspend fun getPrimaryAddress(userId: String): Result<Address?> {
        return try {
            val result = supabase.from("addresses").select {
                filter {
                    eq("user_id", userId)
                    eq("is_primary", true)
                }
            }.decodeSingleOrNull<Address>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
