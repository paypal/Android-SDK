package com.paypal.android.ui.venmo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.paypal.android.VenmoLauncher
import com.paypal.android.databinding.FragmentVenmoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VenmoFragment : Fragment() {

    private lateinit var binding: FragmentVenmoBinding

    private val viewModel: VenmoViewModel by viewModels()

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
        viewModel.progressIndicatorLiveData.observe(viewLifecycleOwner) {
            progressIndicatorVisible = it
        }
        binding.launchVenmoButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                launchVenmo()
            }
        }
    }

    private fun launchVenmo() {
        val venmoLauncher = VenmoLauncher(
            context = requireContext(),
            lifecycle = lifecycle
        )
        val orderId = "9DB4556370505850X"
        venmoLauncher.payWithVenmo(orderId = orderId,
            onSuccess = {
                Log.d("Tag", "venmo success")
            }, onFailure = {
                Log.d("Tag", "venmo failure")
            })
//        viewModel.venmoUrl().observe(viewLifecycleOwner) {
//            val intent = Intent(Intent.ACTION_VIEW, it)
//            requireActivity().startActivity(intent)
//        }
    }
}
