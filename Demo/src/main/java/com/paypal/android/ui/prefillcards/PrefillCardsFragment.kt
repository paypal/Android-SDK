package com.paypal.android.ui.prefillcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.paypal.android.data.card.TestCards
import com.paypal.android.databinding.FragmentPrefillCardsBinding

class PrefillCardsFragment : Fragment() {

    private lateinit var binding: FragmentPrefillCardsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrefillCardsBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = mutableListOf<PrefillCardsItem>()
        for (i in 0 until TestCards.numGroups) {
            val group = TestCards.Group.values()[i]
            items += PrefillCardsItem.Header(group.name)

            val prefillCards = TestCards.cardsInGroup(group)
            items += prefillCards.map { PrefillCardsItem.Data(it) }
        }

        binding.run {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = PrefillCardsAdapter(items)
        }
    }
}