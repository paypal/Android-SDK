package com.paypal.android.checkoutweb

import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.BrowserSwitchClient
import com.braintreepayments.api.BrowserSwitchResult
import com.braintreepayments.api.BrowserSwitchStatus
import com.paypal.android.checkoutweb.errors.PayPalError
import com.paypal.android.core.CoreConfig

/**
 * Use this client to approve an order with a [PayPalRequest].
 */
class PayPalWebClient internal constructor(
    private val activity: FragmentActivity,
    private val coreConfig: CoreConfig,
    private val browserSwitchClient: BrowserSwitchClient,
    private val browserSwitchHelper: BrowserSwitchHelper
) {

    /**
     * Create a new instance of [PayPalWebClient].
     *
     * @param activity a [FragmentActivity]
     * @param configuration a [CoreConfig] object
     * @param urlScheme the custom URl scheme used to return to your app from a browser switch flow
     */
    constructor(
        activity: FragmentActivity,
        configuration: CoreConfig,
        urlScheme: String
    ) : this(activity, configuration, BrowserSwitchClient(), BrowserSwitchHelper(urlScheme))

    private var browserSwitchResult: BrowserSwitchResult? = null

    /**
     * Sets a listener to receive notifications when a PayPal event occurs.
     */
    var listener: PayPalCheckoutListener? = null
        /**
         * @param value a [PayPalCheckoutListener] to receive results from the PayPal flow
         */
        set(value) {
            field = value
            browserSwitchResult?.also {
                handleBrowserSwitchResult()
            }
        }

    init {
        activity.lifecycle.addObserver(PayPalLifeCycleObserver(this))
    }

    /**
     * Confirm PayPal payment source for an order. Result will be delivered to your [PayPalCheckoutListener].
     *
     * @param request [PayPalRequest] for requesting an order approval
     */
    fun approveOrder(request: PayPalRequest) {
        val browserSwitchOptions = browserSwitchHelper.configurePayPalBrowserSwitchOptions(
            request.orderID,
            coreConfig
        )
        browserSwitchClient.start(activity, browserSwitchOptions)
    }

    internal fun handleBrowserSwitchResult() {
        browserSwitchResult = browserSwitchClient.deliverResult(activity)
        listener?.also {
            browserSwitchResult?.also { result ->
                when (result.status) {
                    BrowserSwitchStatus.SUCCESS -> deliverSuccess()
                    BrowserSwitchStatus.CANCELED -> deliverCancellation()
                }
            }
        }
    }

    private fun deliverSuccess() {
        if (browserSwitchResult?.deepLinkUrl != null && browserSwitchResult?.requestMetadata != null) {
            val webResult = PayPalWebResult(
                browserSwitchResult?.deepLinkUrl!!,
                browserSwitchResult?.requestMetadata!!
            )
            if (!webResult.orderId.isNullOrBlank() && !webResult.payerId.isNullOrBlank()) {
                listener?.onPayPalSuccess(
                    PayPalCheckoutResult(
                        webResult.orderId,
                        webResult.payerId
                    )
                )
            } else {
                listener?.onPayPalFailure(PayPalError.malformedResultError)
            }
        } else {
            listener?.onPayPalFailure(PayPalError.unknownError)
        }
        browserSwitchResult = null
    }

    private fun deliverCancellation() {
        browserSwitchResult = null
        listener?.onPayPalCanceled()
    }
}
