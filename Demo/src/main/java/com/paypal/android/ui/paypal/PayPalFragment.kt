package com.paypal.android.ui.paypal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paypal.android.BuildConfig
import com.paypal.android.R
import com.paypal.android.checkout.PayPalClient
import com.paypal.android.checkout.PayPalClientListener
import com.paypal.android.checkout.PayPalConfiguration
import com.paypal.android.checkout.pojo.Approval
import com.paypal.android.checkout.pojo.ErrorInfo
import com.paypal.android.checkout.pojo.ShippingChangeData
import com.paypal.android.core.Environment
import com.paypal.android.ui.theme.DemoTheme

class PayPalFragment : Fragment(), PayPalClientListener {

    private val orderId = ""
    private val clientId = "AXD8q8D3tq0vaZb5j6EFB-HsLdPZXDFmYbnmaJCn40ohZ3CiQnLg2kkx-H_Ze0z8GVfcJ879zA21BeMB"
    private val returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay"

    private lateinit var payPalClient: PayPalClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initPayPalCheckout()
        return ComposeView(requireContext()).apply {
            setContent {
                PayPalFragmentView()
            }
        }
    }

    @Preview
    @Composable
    fun PayPalFragmentView() = DemoTheme {
        Column {
            Button(
                onClick = { launchNativeCheckout() },
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
                    .fillMaxWidth()
            ) { Text(stringResource(R.string.start_checkout)) }
        }
    }

    private fun initPayPalCheckout() {
        val payPalConfiguration = PayPalConfiguration(
            application = requireActivity().application,
            clientId = clientId,
            returnUrl = returnUrl,
            environment = Environment.SANDBOX
        )
        payPalClient = PayPalClient(payPalConfig = payPalConfiguration)
    }

    private fun launchNativeCheckout() {

    }

    override fun onPayPalApprove(approval: Approval) {
        TODO("Not yet implemented")
    }

    override fun onPayPalError(errorInfo: ErrorInfo) {
        TODO("Not yet implemented")
    }

    override fun onPayPalCancel() {
        TODO("Not yet implemented")
    }

    override fun onPayPalShippingAddressChange(
        shippingChangeData: ShippingChangeData,
        shippingChangeActions: com.paypal.checkout.shipping.ShippingChangeActions
    ) {
        TODO("Not yet implemented")
    }
}
