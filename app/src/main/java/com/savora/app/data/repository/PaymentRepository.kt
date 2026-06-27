package com.savora.app.data.repository

import com.savora.app.data.model.Payment
import com.savora.app.remote.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PaymentRepository {

    suspend fun createPayment(payment: JsonObject): Result<Payment> {
        return try {
            val result = supabase.from("payments").insert(payment) { select() }.decodeSingle<Payment>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPayments(userId: String, status: String? = null): Result<List<Payment>> {
        return try {
            val result = supabase.from("payments").select {
                filter { eq("user_id", userId); if (status != null) eq("status", status) }
            }.decodeList<Payment>()
            Result.success(result.sortedByDescending { it.createdAt })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPaymentById(paymentId: String): Result<Payment> {
        return try {
            val result = supabase.from("payments").select { filter { eq("id", paymentId) } }.decodeSingle<Payment>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getPaymentByOrderId(orderId: String): Result<Payment> {
        return try {
            val result = supabase.from("payments").select { filter { eq("order_id", orderId) } }.decodeSingle<Payment>()
            Result.success(result)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updatePaymentStatus(paymentId: String, status: String): Result<Unit> {
        return try {
            supabase.from("payments").update(buildJsonObject { put("status", status) }) { filter { eq("id", paymentId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateMidtransData(paymentId: String, token: String, redirectUrl: String): Result<Unit> {
        return try {
            supabase.from("payments").update(buildJsonObject {
                put("midtrans_token", token); put("midtrans_redirect_url", redirectUrl)
            }) { filter { eq("id", paymentId) } }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}