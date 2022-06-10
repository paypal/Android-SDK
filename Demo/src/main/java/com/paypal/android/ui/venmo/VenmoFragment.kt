package com.paypal.android.ui.venmo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.paypal.android.api.model.Amount
import com.paypal.android.api.model.ApplicationContext
import com.paypal.android.api.model.CreateOrderRequest
import com.paypal.android.api.model.Order
import com.paypal.android.api.model.Payee
import com.paypal.android.api.model.PurchaseUnit
import com.paypal.android.api.services.PayPalDemoApi
import com.paypal.android.card.Card
import com.paypal.android.card.CardRequest
import com.paypal.android.card.threedsecure.SCA
import com.paypal.android.card.threedsecure.ThreeDSecureRequest
import com.paypal.android.databinding.FragmentVenmoBinding
import com.paypal.android.ui.card.CardFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VenmoFragment : Fragment() {

    @Inject
    lateinit var payPalDemoApi: PayPalDemoApi

    private lateinit var binding: FragmentVenmoBinding

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

    private suspend fun launchVenmo() {

        val order = createOrder()

        val uri = Uri.parse("https://www.paypal.com/smart/checkout/venmo")
            .buildUpon()
            .appendQueryParameter("channel", "mobile-web")
            .appendQueryParameter("sessionUID", "NOOP")
            .appendQueryParameter("orderID", order.id!!)
            .appendQueryParameter(
                "facilitatorAccessToken",
                "A21AANJT-DPnNpO6ZGzrjUH7k__21PSBydkdheCtnuCcL6QVD5cUvr_GUr5anpNkEsDo6pRg9wzc3MRNqhK69xJ9qy7YLxerQ"
            )
            .appendQueryParameter(
                "clientID",
                "AdLzRW18VHoABXiBhpX2gf0qhXwiW4MmFVHL69V90vciCg_iBLGyJhlf7EuWtFcdNjGiDfrwe7rmhvMZ"
            )
            .appendQueryParameter("commit", "false")
            .appendQueryParameter("webCheckoutUrl", "")
            .appendQueryParameter("buttonSessionID", "NOOP")
            .appendQueryParameter("env", "sandbox")
            .appendQueryParameter("fundingSource", "venmo")
            .appendQueryParameter("enableFunding", "venmo")
            .appendQueryParameter("buyerCountry", "US")
            .appendQueryParameter("pageUrl", "www.google.com")
            .appendQueryParameter("sdkVersion", "5.0.304")
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
