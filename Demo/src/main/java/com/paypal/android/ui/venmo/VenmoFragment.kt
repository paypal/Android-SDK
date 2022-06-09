package com.paypal.android.ui.venmo

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
}
