package com.paypal.android.checkout

import com.paypal.android.core.PayPalSDKError

/**
 * Implement this callback to receive results from [PayPalClient].
 */
interface PayPalListener {

    /**
     * Called when the PayPal flow completes successfully.
     * @param result [PayPalCheckoutResult] with order information.
     */
    fun onPayPalSuccess(result: PayPalCheckoutResult)

    /**
     * Called when the PayPal flow completes with an error.
     * @param error [PayPalSDKError] explaining the reason for failure.
     */
    fun onPayPalFailure(error: PayPalSDKError)

    /**
     * Called when the PayPal flow was canceled by the user.
     */
    fun onPayPalCanceled()
}
