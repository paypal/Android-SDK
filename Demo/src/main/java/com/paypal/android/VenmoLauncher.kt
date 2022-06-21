package com.paypal.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

//ToDo: move to venmo module
//ToDo: Create order from here, by taking required params

class VenmoLauncher(
    private val context: Context,
    lifecycle: Lifecycle
) : LifecycleEventObserver {
    private var onSuccess: (() -> Unit)? = null
    private var onFailure: (() -> Unit)? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event.targetState == Lifecycle.State.RESUMED) {
            //todo: get status of order/payment
            onSuccess?.invoke()
        }
    }

    fun payWithVenmo(orderId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        this.onSuccess = onSuccess
        this.onFailure = onFailure
        launchVenmo(getVenmoUrl(orderId))
    }

    private fun launchVenmo(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    private fun getVenmoUrl(orderId: String): Uri =
        Uri.parse("https://www.paypal.com/smart/checkout/venmo")
            .buildUpon()
            .appendQueryParameter("orderID", orderId)
            .appendQueryParameter("buyerCountry", "US")
            .appendQueryParameter("sessionUID", "NOOP")
            .appendQueryParameter("buttonSessionID", "NOOP")
            .appendQueryParameter("channel", "mobile-web")
            .appendQueryParameter("clientID", clientId)
            .appendQueryParameter("facilitatorAccessToken", facilitatorAccessToken)
            .appendQueryParameter("commit", "false")
            .appendQueryParameter("env", com.paypal.android.core.Environment.SANDBOX.name)
            .build()

    companion object {
        const val TIME_OUT_IN_SECONDS = "50"
        //todo: remove all below hardcodings

        const val clientId =
            "AdLzRW18VHoABXiBhpX2gf0qhXwiW4MmFVHL69V90vciCg_iBLGyJhlf7EuWtFcdNjGiDfrwe7rmhvMZ"

        //todo: how to get facilitatorAccessToken without hardcoding
        const val facilitatorAccessToken =
            "A21AAIAtIdiqxhfvvlooEpq_KRAcU3D2S8IoPTVgaUa9TXg-dar59ZXl6Cu718cIh_bc7k7PPymTTn9KPwbapnnD-AdhivC6w" //fetchAuthToken()
    }
}
