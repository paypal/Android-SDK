package com.paypal.android.ui.venmo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.paypal.android.BuildConfig
import com.paypal.android.api.model.Amount
import com.paypal.android.api.model.CreateOrderRequest
import com.paypal.android.api.model.Order
import com.paypal.android.api.model.Payee
import com.paypal.android.api.model.PurchaseUnit
import com.paypal.android.api.services.AuthApi
import com.paypal.android.api.services.PayPalDemoApi
import com.paypal.android.core.CoreConfig
import com.paypal.android.databinding.FragmentVenmoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VenmoFragment : Fragment() {

    @Inject
    lateinit var payPalDemoApi: PayPalDemoApi

    @Inject
    lateinit var authApi: AuthApi

    private val config = CoreConfig(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

    private lateinit var binding: FragmentVenmoBinding

    var progressIndicatorVisible: Boolean
        get() = (binding.progressIndicator.visibility == View.VISIBLE)
        set(isVisible) {
            binding.progressIndicator.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVenmoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.launchVenmoButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                launchVenmo()
            }
        }
    }

    private fun launchVenmo() {
        progressIndicatorVisible = true
//        val order = withContext(IO){
//            createOrder()
//        }
        val facilitatorAccessToken =
            "A21AANJT-DPnNpO6ZGzrjUH7k__21PSBydkdheCtnuCcL6QVD5cUvr_GUr5anpNkEsDopRg9wzc3MRNqhK69xJ9qy7YLxerQ" //authApi.postOAuthToken().accessToken
        val clientId =
            "AdLzRW18VHoABXiBhpX2gf0qhXwiW4MmFVHL69V90vciCg_iBLGyJhlf7EuWtFcdNjGiDfrwe7rmhvMZ"
        val orderId = "68B05296CR090552C"
        progressIndicatorVisible = false
        val uri = Uri.parse("https://www.paypal.com/smart/checkout/venmo")
            .buildUpon()
            .appendQueryParameter("orderID", orderId)
            //.appendQueryParameter("enableFunding", "venmo")
            //.appendQueryParameter("fundingSource", "venmo")
            .appendQueryParameter("buyerCountry", "US")
            .appendQueryParameter("sessionUID", "NOOP")
            .appendQueryParameter("buttonSessionID", "NOOP")
            //.appendQueryParameter("channel", "mobile-web")
            .appendQueryParameter("clientID", clientId)
            .appendQueryParameter("facilitatorAccessToken", facilitatorAccessToken)
            .appendQueryParameter("commit", "false")
            .appendQueryParameter("env", "Sandbox")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireActivity().startActivity(intent)
    }

    private suspend fun createOrder(): Order {
        val orderRequest = CreateOrderRequest(
            intent = "AUTHORIZE",
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

        return payPalDemoApi.fetchOrderId(countryCode = "CO", orderRequest = orderRequest)
    }
}
