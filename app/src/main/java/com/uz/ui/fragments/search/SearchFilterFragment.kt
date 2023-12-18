package com.uz.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.databinding.FragmentSearchFilterBinding
import com.uz.ui.base.BaseFragment

class SearchFilterFragment : BaseFragment<FragmentSearchFilterBinding>() {
    override val layId: Int
        get() = R.layout.fragment_search_filter

    private var filter: BusinessController.Business.BusinessFilter =
        BusinessController.Business.BusinessFilter.Mix

    companion object {
        const val FILTER_KEY = "filter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filter =
            BusinessController.Business.BusinessFilter.valueOf(
                arguments?.getString(FILTER_KEY) ?: ""
            )
    }

    private val layIds = mapOf(
        Pair(BusinessController.Business.BusinessFilter.PriceCheap, R.id.cheap_chip),
        Pair(BusinessController.Business.BusinessFilter.Mix, R.id.mix_chip),
        Pair(BusinessController.Business.BusinessFilter.Populars, R.id.top_chip)
    )

    override fun viewCreated(bind: FragmentSearchFilterBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@SearchFilterFragment)
            val currentFilter = filter
            filterChip.check(layIds.getOrDefault(filter, R.id.mix_chip))
            filterChip.setOnCheckedStateChangeListener { group, checkedIds ->
                val checked = group.checkedChipId
                filter = when (checked) {
                    R.id.cheap_chip -> BusinessController.Business.BusinessFilter.PriceCheap
                    R.id.top_chip -> BusinessController.Business.BusinessFilter.Populars
                    R.id.mix_chip -> BusinessController.Business.BusinessFilter.Mix
                    else -> filter
                }
            }

            doneButton.setOnClickListener {
                if (currentFilter != filter) {
                    setFragmentResult(FILTER_KEY, Bundle().apply {
                        putString(FILTER_KEY, filter.name)
                    })
                }
                findNavController().popBackStack()
            }
        }
    }
}