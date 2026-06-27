package com.savora.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savora.app.data.model.PaymentWithOrder
import com.savora.app.data.repository.OrderRepository
import com.savora.app.data.repository.PaymentRepository
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val payments: List<PaymentWithOrder> = emptyList(),
    val selectedFilter: String = "Semua",
    val error: String? = null,
)

class PaymentHistoryViewModel : ViewModel() {
    private val paymentRepo = PaymentRepository()
    private val orderRepo = OrderRepository()

    private val _uiState = MutableStateFlow(PaymentHistoryUiState())
    val uiState: StateFlow<PaymentHistoryUiState> = _uiState

    val filters = listOf("Semua", "Berhasil", "Pending", "Gagal")

    init {
        loadPayments()
    }

    fun loadPayments(statusFilter: String = "Semua") {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, selectedFilter = statusFilter)

            // Status di DB: "success" / "pending" / "failed"
            val dbStatus = when (statusFilter) {
                "Berhasil" -> "success"
                "Pending"  -> "pending"
                "Gagal"    -> "failed"
                else       -> null
            }

            val paymentsResult = paymentRepo.getPayments(userId, dbStatus)
            val payments = paymentsResult.getOrDefault(emptyList())

            // Ambil semua orders 1x, bukan N kali di dalam loop
            val allOrders = orderRepo.getOrders(userId).getOrDefault(emptyList())
            val ordersById = allOrders.associateBy { it.id }

            val paymentsWithOrders = payments.mapNotNull { payment ->
                val order = ordersById[payment.orderId] ?: return@mapNotNull null
                val items = orderRepo.getOrderItems(order.id).getOrDefault(emptyList())
                PaymentWithOrder(payment, order, items)
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                payments = paymentsWithOrders,
                error = paymentsResult.exceptionOrNull()?.message
            )
        }
    }

    fun filterByStatus(status: String) {
        loadPayments(status)
    }
}
