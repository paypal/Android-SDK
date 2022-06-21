package com.paypal.android.ui.venmo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paypal.android.api.model.Amount
import com.paypal.android.api.model.CreateOrderRequest
import com.paypal.android.api.model.Order
import com.paypal.android.api.model.Payee
import com.paypal.android.api.model.PurchaseUnit
import com.paypal.android.api.services.AuthApi
import com.paypal.android.api.services.PayPalDemoApi
import com.paypal.android.core.CoreConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VenmoViewModel @Inject constructor(
    private val payPalDemoApi: PayPalDemoApi,
    private val authApi: AuthApi,
    private val config: CoreConfig
) : ViewModel() {

    val progressIndicatorLiveData = MutableLiveData(false)
    private suspend fun createOrder(): Order = withContext(Dispatchers.IO) {
        val orderRequest = CreateOrderRequest(
            intent = "CAPTURE",
            purchaseUnit = listOf(
                PurchaseUnit(
                    amount = Amount(
                        currencyCode = "USD",
                        value = "10.99"
                    )
                )
            ),
            payee = Payee(emailAddress = "brianatree@paypal.com")
        )
        payPalDemoApi.fetchOrderId(countryCode = "CO", orderRequest = orderRequest)
    }

    private suspend fun fetchAuthToken(): String = withContext(Dispatchers.IO) {
        authApi.postOAuthToken().accessToken
    }

    fun venmoUrl(): LiveData<Uri> {
        val liveData: MutableLiveData<Uri> = MutableLiveData()
        viewModelScope.launch {
            progressIndicatorLiveData.postValue(true)

            val facilitatorAccessToken =
                "A21AAIAtIdiqxhfvvlooEpq_KRAcU3D2S8IoPTVgaUa9TXg-dar59ZXl6Cu718cIh_bc7k7PPymTTn9KPwbapnnD-AdhivC6w" //fetchAuthToken()
            val orderId = createOrder().id
            val uri = Uri.parse("https://www.paypal.com/smart/checkout/venmo")
                .buildUpon()
                .appendQueryParameter("orderID", orderId)
                .appendQueryParameter("buyerCountry", "US")
                .appendQueryParameter("sessionUID", "NOOP")
                .appendQueryParameter("buttonSessionID", "NOOP")
                //.appendQueryParameter("channel", "mobile-web")
                .appendQueryParameter("clientID", config.clientId)
                .appendQueryParameter("facilitatorAccessToken", facilitatorAccessToken)
                .appendQueryParameter("commit", "false")
                .appendQueryParameter("env", config.environment.name)
                .build()
            progressIndicatorLiveData.postValue(false)
            liveData.postValue(uri)
        }
        return liveData
    }
}
