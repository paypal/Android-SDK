package com.paypal.android.checkout

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.paypal.android.checkout.pojo.Buyer
import com.paypal.android.checkout.pojo.ErrorInfo
import com.paypal.android.checkout.pojo.ShippingAddress
import com.paypal.android.core.CoreConfig
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.error.OnError

@RequiresApi(Build.VERSION_CODES.M)
class PayPalClient(application: Application, coreConfig: CoreConfig, returnUrl: String) {

    init {
        val config = CheckoutConfig(
            application = application,
            clientId = coreConfig.clientId,
            environment = getPayPalEnvironment(coreConfig.environment),
            returnUrl = returnUrl,
        )
        PayPalCheckout.setConfig(config)
    }

    fun checkout(orderId: String, complete: (PayPalCheckoutResult) -> Unit) {
        PayPalCheckout.start(CreateOrder { createOrderActions ->
            createOrderActions.set(orderId)
        },
            onApprove = OnApprove { approval ->
                complete(
                    PayPalCheckoutResult.Success(
                        approval.data.orderId,
                        approval.data.payerId,
                        Buyer(approval.data.payer),
                        ShippingAddress(approval.data.cart?.shippingAddress)
                    )
                )
            },
            onCancel = OnCancel {
                complete(PayPalCheckoutResult.Cancellation)
            },
            onError = OnError { errorInfo ->
                complete(PayPalCheckoutResult.Failure(ErrorInfo(errorInfo)))
            })
    }
}
