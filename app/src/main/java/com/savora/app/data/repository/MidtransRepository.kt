package com.savora.app.data.repository

import android.util.Base64
import android.util.Log
import com.savora.app.remote.MidtransConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class MidtransSnapResult(val token: String, val redirectUrl: String)
data class MidtransTransactionStatus(val transactionStatus: String, val fraudStatus: String?)

class MidtransRepository {

    private fun buildCredentials(): String = Base64.encodeToString(
        "${MidtransConfig.SERVER_KEY.trim()}:".toByteArray(Charsets.UTF_8), Base64.NO_WRAP,
    )

    suspend fun getTransactionStatus(orderId: String): Result<MidtransTransactionStatus> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.sandbox.midtrans.com/v2/$orderId/status")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Basic ${buildCredentials()}")
                conn.setRequestProperty("Accept", "application/json")
                conn.connectTimeout = 15_000; conn.readTimeout = 15_000
                val responseCode = conn.responseCode
                val body = if (responseCode == 200) conn.inputStream.bufferedReader().readText()
                else {
                    val err = conn.errorStream?.bufferedReader()?.readText() ?: "Unknown"
                    Log.e("MidtransRepo", "Status error $responseCode: $err")
                    return@withContext Result.failure(Exception("Status error $responseCode"))
                }
                val json = JSONObject(body)
                Result.success(MidtransTransactionStatus(
                    transactionStatus = json.optString("transaction_status", "unknown"),
                    fraudStatus = json.optString("fraud_status").takeIf { it.isNotEmpty() },
                ))
            } catch (e: Exception) { Result.failure(e) }
        }

    suspend fun getSnapToken(orderId: String, grossAmount: Long, customerName: String, items: List<Pair<String, Long>>): Result<MidtransSnapResult> =
        withContext(Dispatchers.IO) {
            try {
                val itemArray = JSONArray()
                items.forEach { (name, price) ->
                    itemArray.put(JSONObject().apply {
                        put("id", name.lowercase().replace(" ", "_"))
                        put("price", price); put("quantity", 1); put("name", name)
                    })
                }
                val body = JSONObject().apply {
                    put("transaction_details", JSONObject().apply { put("order_id", orderId); put("gross_amount", grossAmount) })
                    put("customer_details", JSONObject().apply { put("first_name", customerName) })
                    put("item_details", itemArray)
                }.toString()
                val url = URL(MidtransConfig.SNAP_BASE_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Basic ${buildCredentials()}")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true; conn.connectTimeout = 15_000; conn.readTimeout = 15_000
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val responseCode = conn.responseCode
                val responseBody = if (responseCode == 201) conn.inputStream.bufferedReader().readText()
                else {
                    val err = conn.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                    Log.e("MidtransRepo", "Error $responseCode: $err")
                    return@withContext Result.failure(Exception("Midtrans error $responseCode: $err"))
                }
                val json = JSONObject(responseBody)
                Result.success(MidtransSnapResult(token = json.getString("token"), redirectUrl = json.getString("redirect_url")))
            } catch (e: Exception) { Result.failure(e) }
        }
}