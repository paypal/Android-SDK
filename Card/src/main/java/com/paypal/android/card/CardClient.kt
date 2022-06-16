package com.paypal.android.card

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.BrowserSwitchClient
import com.braintreepayments.api.BrowserSwitchOptions
import com.braintreepayments.api.BrowserSwitchResult
import com.braintreepayments.api.BrowserSwitchStatus
import com.paypal.android.card.api.CardAPI
import com.paypal.android.card.api.GetOrderRequest
import com.paypal.android.card.model.CardResult
import com.paypal.android.core.API
import com.paypal.android.core.APIRequest
import com.paypal.android.core.CoreConfig
import com.paypal.android.core.HttpMethod
import com.paypal.android.core.HttpRequest
import com.paypal.android.core.PayPalSDKError
import com.paypal.android.core.PaymentsJSON
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.util.*

/**
 * Use this client to approve an order with a [Card].
 */
class CardClient internal constructor(
    activity: FragmentActivity,
    private val cardAPI: CardAPI,
    private val browserSwitchClient: BrowserSwitchClient,
    private val dispatcher: CoroutineDispatcher
) {

    var approveOrderListener: ApproveOrderListener? = null

    private val lifeCycleObserver = CardLifeCycleObserver(this)

    /**
     *  CardClient constructor
     *
     *  @param [activity] Activity that launches the card client
     *  @param [configuration] Configuration parameters for client
     */
    constructor(activity: FragmentActivity, configuration: CoreConfig) :
            this(activity, CardAPI(API(configuration)), BrowserSwitchClient(), Dispatchers.Main)

    init {
        activity.lifecycle.addObserver(lifeCycleObserver)
    }

    /**
     * Confirm [Card] payment source for an order.
     *
     * @param activity [FragmentActivity] activity used to start 3DS flow (if requested)
     * @param cardRequest [CardRequest] for requesting an order approval
     */
    fun approveOrder(activity: FragmentActivity, cardRequest: CardRequest) {
        CoroutineScope(dispatcher).launch {
            try {
                confirmPaymentSource(activity, cardRequest)
            } catch (e: PayPalSDKError) {
                approveOrderListener?.onApproveOrderFailure(e)
            }
        }
    }

    private suspend fun confirmPaymentSource(activity: FragmentActivity, cardRequest: CardRequest) {
        val response = cardAPI.confirmPaymentSource(cardRequest)
        if (response.authorizeHref != null) {

            val requestID = UUID.randomUUID().toString()

            // authorize order
//            val headers = mutableMapOf("PayPal-Request-Id" to requestID)
//            val authorizeRequest = HttpRequest(URL(response.authorizeHref), HttpMethod.POST, "{}", headers)
//            val authorizeResponse = cardAPI.send(authorizeRequest)
//
//            Log.d("TAG", authorizeResponse.headers.toString())
//
//            val bodyResponse = authorizeResponse.body!!
//            val authorizeJSON = PaymentsJSON(bodyResponse)

            // fetch full scoped access token
            val fsatRequest = APIRequest(
                "v1/oauth2/token",
                HttpMethod.POST,
                "grant_type=client_credentials&response_type=token&return_authn_schemes=true",
                "application/x-www-form-urlencoded"
            )
            val fsatResponse = cardAPI.send(fsatRequest)

            val fsatBody = fsatResponse.body!!
            val fsatJSON = PaymentsJSON(fsatBody)
            val fsatToken = fsatJSON.getString("access_token")

            Log.d("TAG", fsatResponse.toString())

//            authorizeJSON.getLinkHref("confirm-payment-token")?.let { confirmHref ->
//
//                val confirmHeaders = mutableMapOf("PayPal-Request-Id" to requestID)
//                val confirmRequest = HttpRequest(URL(confirmHref), HttpMethod.POST, null, confirmHeaders)
//                confirmRequest.authOverride = fsatToken
//                val confirmResponse = cardAPI.send(confirmRequest)
//
//                Log.d("TAG", confirmResponse.toString())
//            }

            // list vaulted payment methods
            val getPaymentTokensRequest =
                APIRequest("v3/vault/payment-tokens?customer_id=123", HttpMethod.GET, null, "application/json", fsatToken)
            val paymentTokensResponse = cardAPI.send(getPaymentTokensRequest)
            Log.d("TAG", paymentTokensResponse.toString())

        } else if (response.payerActionHref == null) {
            val result = response.run {
                CardResult(orderID, status, paymentSource)
            }
            approveOrderListener?.onApproveOrderSuccess(result)
        } else {
            approveOrderListener?.onApproveOrderThreeDSecureWillLaunch()

            // launch the 3DS flow
            val approveOrderMetadata =
                ApproveOrderMetadata(cardRequest.orderID, response.paymentSource)
            val options = BrowserSwitchOptions()
                .url(Uri.parse(response.payerActionHref))
                .returnUrlScheme("com.paypal.android.demo")
                .metadata(approveOrderMetadata.toJSON())

            browserSwitchClient.start(activity, options)
        }
    }

    internal fun handleBrowserSwitchResult(activity: FragmentActivity) {
        val browserSwitchResult = browserSwitchClient.deliverResult(activity)
        if (browserSwitchResult != null && approveOrderListener != null) {
            approveOrderListener?.onApproveOrderThreeDSecureDidFinish()
            when (browserSwitchResult.status) {
                BrowserSwitchStatus.SUCCESS -> getOrderInfo(browserSwitchResult)
                BrowserSwitchStatus.CANCELED -> notifyApproveOrderCanceled()
            }
        }
    }

    private fun getOrderInfo(browserSwitchResult: BrowserSwitchResult) {
        ApproveOrderMetadata.fromJSON(browserSwitchResult.requestMetadata)?.let { metadata ->
            CoroutineScope(dispatcher).launch {
                try {
                    val getOrderResponse = cardAPI.getOrderInfo(GetOrderRequest(metadata.orderID))
                    approveOrderListener?.onApproveOrderSuccess(
                        CardResult(
                            getOrderResponse.orderId,
                            getOrderResponse.orderStatus,
                            metadata.paymentSource,
                            browserSwitchResult.deepLinkUrl
                        )
                    )
                } catch (e: PayPalSDKError) {
                    approveOrderListener?.onApproveOrderFailure(e)
                }
            }
        }
    }

    private fun notifyApproveOrderCanceled() {
        approveOrderListener?.onApproveOrderCanceled()
    }
}
