package com.paypal.android.ui.paypal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.paypal.android.api.model.Order
import com.paypal.android.api.services.PayPalDemoApi
import com.paypal.android.checkout.PayPalCheckoutListener
import com.paypal.android.checkout.PayPalCheckoutResult
import com.paypal.android.checkout.PayPalClient
import com.paypal.android.checkout.PayPalRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PayPalViewModel @Inject constructor(
    private val payPalDemoApi: PayPalDemoApi
) : ViewModel() {

    companion object {
        private val TAG = PayPalViewModel::class.qualifiedName
    }

    private val orderJson = OrderUtils.orderWithShipping
    private var payPalClient: PayPalClient? = null

    private val _checkoutResult = MutableLiveData<PayPalCheckoutResult>()
    val checkoutResult: LiveData<PayPalCheckoutResult> = _checkoutResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun startPayPalCheckout() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val order = fetchOrder()
                order.id?.let { orderId ->
                    val payPalRequest = PayPalRequest(orderId)
                    payPalClient?.approveOrder(payPalRequest)
                }
            } catch (e: UnknownHostException) {
                Log.e(TAG, e.message!!)
                _isLoading.value = false
            } catch (e: HttpException) {
                Log.e(TAG, e.message!!)
                _isLoading.value = false
            }
        }
    }

    fun setPayPalClient(payPalClient: PayPalClient) {
        this.payPalClient = payPalClient
        this.payPalClient?.listener = PayPalCheckoutListener { result ->
            when (result) {
                is PayPalCheckoutResult.Success -> Log.i(
                    TAG,
                    "Order Approved: ${result.orderId} && ${result.payerId}"
                )
                is PayPalCheckoutResult.Failure -> Log.i(
                    TAG,
                    "Checkout Error: ${result.error.message}"
                )
                is PayPalCheckoutResult.Cancellation -> Log.i(TAG, "User cancelled")
            }
            _checkoutResult.value = result
            _isLoading.value = false
        }
    }

    private suspend fun fetchOrder(): Order {
        val parser = JsonParser()
        val orderJson = parser.parse(orderJson) as JsonObject
        return payPalDemoApi.fetchOrderId(countryCode = "US", orderJson)
    }
}
