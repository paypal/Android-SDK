package com.paypal.android.ui.venmo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paypal.android.databinding.FragmentVenmoBinding

class VenmoFragment : Fragment() {

    private lateinit var binding: FragmentVenmoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVenmoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            launchVenmoButton.setOnClickListener { launchVenmo() }
        }
    }

    private fun launchVenmo() {

        val uri = Uri.parse("https://www.paypal.com/smart/checkout/venmo")
            .buildUpon()
            .appendQueryParameter("channel", "mobile-web")
            .appendQueryParameter("sessionUID", "NOOP")
            .appendQueryParameter("orderID", "3VW30466KR0605801")
            .appendQueryParameter("facilitatorAccessToken", "A21AANJT-DPnNpO6ZGzrjUH7k__21PSBydkdheCtnuCcL6QVD5cUvr_GUr5anpNkEsDo6pRg9wzc3MRNqhK69xJ9qy7YLxerQ")
            .appendQueryParameter("clientID", "AdLzRW18VHoABXiBhpX2gf0qhXwiW4MmFVHL69V90vciCg_iBLGyJhlf7EuWtFcdNjGiDfrwe7rmhvMZ")
            .appendQueryParameter("commit", "false")
            .appendQueryParameter("webCheckoutUrl", "")
            .appendQueryParameter("buttonSessionID", "NOOP")
            .appendQueryParameter("env=production", "")
            .appendQueryParameter("fundingSource", "venmo")
            .appendQueryParameter("enableFunding", "venmo")
            .appendQueryParameter("buyerCountry", "US")
            .appendQueryParameter("pageUrl", "www.google.com")
            .appendQueryParameter("sdkVersion", "5.0.304")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireActivity().startActivity(intent)
    }
}
