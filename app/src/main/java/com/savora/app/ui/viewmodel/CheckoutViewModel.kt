package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.repository.CartRepository
import com.savora.app.data.repository.MidtransRepository
import com.savora.app.data.repository.OrderRepository
import com.savora.app.data.repository.PaymentRepository
import com.savora.app.data.repository.ProductRepository
import com.savora.app.data.repository.UserRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val selectedPaymentMethod: String? = null,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 15000.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val error: String? = null,
    // Midtrans flow
    val midtransUrl: String? = null,
    val waitingForPayment: Boolean = false,
    val pendingPaymentId: String? = null,
    val pendingOrderId: String? = null,
    val pendingOrderNumber: String? = null,
    val pendingMethod: String? = null,
    val pendingTotal: Long = 0L,
)

class CheckoutViewModel : ViewModel() {

    private val cartRepo = CartRepository()
    private val productRepo = ProductRepository()
    private val orderRepo = OrderRepository()
    private val paymentRepo = PaymentRepository()
    private val userRepo = UserRepository()
    private val midtransRepo = MidtransRepository()

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    init { loadCartSummary() }

    private fun loadCartSummary() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val cartItems = cartRepo.getCartItems(userId).getOrDefault(emptyList())
            if (cartItems.isEmpty()) return@launch

            var subtotal = 0.0
            for (item in cartItems) {
                val product = productRepo.getProductById(item.productId).getOrNull()
                if (product != null) subtotal += product.price * item.quantity
            }
            val deliveryFee = 15000.0
            _uiState.value = _uiState.value.copy(
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = subtotal + deliveryFee - _uiState.value.discount,
            )
        }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun clearMidtransUrl() {
        _uiState.value = _uiState.value.copy(midtransUrl = null)
    }

    fun promptPaymentConfirmation(onSuccess: (orderId: String, paymentMethod: String, total: Long) -> Unit) {
        if (!_uiState.value.waitingForPayment) return
        val orderId = _uiState.value.pendingOrderId ?: return
        val orderNumber = _uiState.value.pendingOrderNumber ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val statusResult = midtransRepo.getTransactionStatus(orderNumber)
            _uiState.value = _uiState.value.copy(isLoading = false)

            val status = statusResult.getOrNull()?.transactionStatus
            when (status) {
                "settlement", "capture" -> {
                    val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                    val paymentId = _uiState.value.pendingPaymentId ?: return@launch
                    val method = _uiState.value.pendingMethod ?: return@launch
                    val total = _uiState.value.pendingTotal
                    paymentRepo.updatePaymentStatus(paymentId, "success")
                    orderRepo.updateOrderStatus(orderId, "delivered")
                    cartRepo.clearCart(userId)
                    _uiState.value = _uiState.value.copy(
                        waitingForPayment = false,
                        pendingPaymentId = null,
                        pendingOrderId = null,
                        pendingOrderNumber = null,
                        pendingMethod = null,
                        pendingTotal = 0L,
                    )
                    onSuccess(orderId, method, total)
                }
                "pending" -> {
                    _uiState.value = _uiState.value.copy(
                        error = "Pembayaran belum selesai. Selesaikan di browser lalu kembali ke sini.",
                    )
                }
                null -> {
                    _uiState.value = _uiState.value.copy(
                        error = "Belum dapat mengecek status pembayaran. Coba lagi setelah selesai bayar.",
                    )
                }
                else -> {
                    val paymentId = _uiState.value.pendingPaymentId ?: return@launch
                    paymentRepo.updatePaymentStatus(paymentId, "failed")
                    _uiState.value = _uiState.value.copy(
                        waitingForPayment = false,
                        pendingPaymentId = null,
                        pendingOrderId = null,
                        pendingOrderNumber = null,
                        pendingMethod = null,
                        pendingTotal = 0L,
                        error = "Pembayaran $status. Silakan coba lagi.",
                    )
                }
            }
        }
    }

    fun processPayment(
        onSuccess: (orderId: String, paymentMethod: String, total: Long) -> Unit,
        getOrNull: Unit.() -> Unit
    ) {
        val method = _uiState.value.selectedPaymentMethod ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Sesi berakhir, silakan login kembali")

                val address = userRepo.getPrimaryAddress(userId).getOrNull()
                    ?: throw Exception("Tambahkan alamat pengiriman di Profil terlebih dahulu")

                val cartItems = cartRepo.getCartItems(userId).getOrElse {
                    throw Exception("Gagal membaca keranjang: ${it.message}")
                }
                if (cartItems.isEmpty()) throw Exception("Keranjang kosong")

                val itemsWithProduct = cartItems.mapNotNull { item ->
                    productRepo.getProductById(item.productId).getOrNull()?.let { Pair(item, it) }
                }

                val subtotal = itemsWithProduct.sumOf { (item, product) -> product.price * item.quantity }
                val deliveryFee = _uiState.value.deliveryFee
                val discount = _uiState.value.discount
                val total = subtotal + deliveryFee - discount
                val orderNumber = "SAV-${System.currentTimeMillis().toString().takeLast(8)}"

                // 1. Buat order
                val order = orderRepo.createOrder(buildJsonObject {
                    put("user_id", userId)
                    put("address_id", address.id)
                    put("order_number", orderNumber)
                    put("status", "pending")
                    put("subtotal", subtotal)
                    put("delivery_fee", deliveryFee)
                    put("discount", discount)
                    put("total", total)
                }).getOrElse { throw Exception("Gagal membuat pesanan: ${it.message}") }

                // 2. Buat order items
                orderRepo.createOrderItems(itemsWithProduct.map { (item, product) ->
                    buildJsonObject {
                        put("order_id", order.id)
                        put("product_id", product.id)
                        put("product_name", product.name)
                        put("price_at_purchase", product.price)
                        put("quantity", item.quantity)
                        put("subtotal", product.price * item.quantity)
                        if (product.imageUrl != null) put("product_image", product.imageUrl)
                        if (item.recipeId != null) put("recipe_id", item.recipeId)
                    }
                }).getOrElse { throw Exception("Gagal menyimpan item pesanan: ${it.message}") }

                // 3. Buat payment record (status: pending)
                val payment = paymentRepo.createPayment(buildJsonObject {
                    put("order_id", order.id)
                    put("user_id", userId)
                    put("payment_method", method)
                    put("status", "pending")
                    put("amount", total)
                }).getOrElse { throw Exception("Gagal memproses pembayaran: ${it.message}") }

                // 4. Minta Snap token ke Midtrans
                val snapItems = itemsWithProduct.map { (item, product) ->
                    product.name to (product.price * item.quantity).toLong()
                } + listOf("Biaya Pengiriman" to deliveryFee.toLong())

                val snapResult = midtransRepo.getSnapToken(
                    orderId = orderNumber,
                    grossAmount = total.toLong(),
                    customerName = address.recipientName,
                    items = snapItems,
                )

                if (snapResult.isSuccess) {
                    val snap = snapResult.getOrThrow()
                    paymentRepo.updateMidtransData(payment.id, snap.token, snap.redirectUrl)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        midtransUrl = snap.redirectUrl,
                        waitingForPayment = true,
                        pendingPaymentId = payment.id,
                        pendingOrderId = order.id,
                        pendingOrderNumber = orderNumber,
                        pendingMethod = method,
                        pendingTotal = total.toLong(),
                    )
                } else {
                    // Midtrans gagal → tandai payment failed, jangan anggap sukses
                    paymentRepo.updatePaymentStatus(payment.id, "failed")
                    val errMsg = snapResult.exceptionOrNull()?.message ?: "Koneksi ke Midtrans gagal"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Pembayaran gagal: $errMsg",
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Terjadi kesalahan, coba lagi",
                )
            }
        }
    }

}
