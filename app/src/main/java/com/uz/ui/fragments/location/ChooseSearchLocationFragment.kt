package com.uz.ui.fragments.location

import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentLocationSearchFragmentBinding
import com.uz.nikoh.databinding.TextCheckItemBinding
import com.uz.nikoh.location.City
import com.uz.nikoh.location.LocationHelper
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.SelectableAdapter
import com.uz.ui.utils.visibleOrGone

class ChooseSearchLocationFragment :
    BaseFragment<FragmentLocationSearchFragmentBinding>() {

    override val layId: Int
        get() = R.layout.fragment_location_search_fragment

    private var selectedCity = LocationHelper.getSearchLocationCity()

    private val adapterR = object :
        SelectableAdapter<City, TextCheckItemBinding>(
            false,
            R.layout.text_check_item,
            EmptyDiffUtil()
        ) {
        override fun onSelected(pos: Int, selected: Boolean) {
            if (selected) {
                val item = getItem(pos)
                selectedCity = item
            }
        }

        override fun bind(holder: ViewHolder<*>, model: City, pos: Int, selected: Boolean) {
            holder.apply {
                binding.apply {
                    (this as TextCheckItemBinding)
                    titleView.text = getString(model.resIdName)
                    checkBox.visibleOrGone(selected)
                    checkBox.isChecked = true
                }
            }
        }

    }


    override fun viewCreated(bind: FragmentLocationSearchFragmentBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@ChooseSearchLocationFragment)
            recyclerView.apply {
                adapter = adapterR.apply {
                    submitList(City.entries)
                    setSelected(City.entries.indexOf(LocationHelper.getSearchLocationCity()))
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                saveButton.setOnClickListener {
                    if (LocationHelper.getSearchLocationCity() != selectedCity) {
                        LocationHelper.setSearchLocation(selectedCity.name)
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

}