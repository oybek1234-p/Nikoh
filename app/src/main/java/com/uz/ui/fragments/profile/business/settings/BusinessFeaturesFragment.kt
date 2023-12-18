package com.uz.ui.fragments.profile.business.settings

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.category.BusinessFeature
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentBusinessSubcategoriesBinding
import com.uz.nikoh.databinding.TextCheckItemBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.SelectableAdapter

class BusinessFeaturesFragment : BaseFragment<FragmentBusinessSubcategoriesBinding>() {

    override val layId: Int
        get() = R.layout.fragment_business_subcategories

    private val adapterS = object : SelectableAdapter<BusinessFeature, TextCheckItemBinding>(
        true, R.layout.text_check_item, EmptyDiffUtil()
    ) {
        override fun bind(
            holder: ViewHolder<*>, model: BusinessFeature, pos: Int, selected: Boolean
        ) {
            holder.binding.apply {
                if (this is TextCheckItemBinding) {
                    checkBox.isChecked = selected
                    titleView.text = getString(model.nameResId)
                }
            }
        }

        override fun onSelected(pos: Int, selected: Boolean) {
            binding?.saveButton?.isEnabled = selectedPositions.isEmpty().not() && changed()
        }
    }

    private val catId = CurrentUser.businessOwner!!.business!!.categoryId
    private val subCats = Categories.getFeatures(catId)

    private fun selected() = adapterS.selectedPositions.mapNotNull { subCats.getOrNull(it)?.name }

    private val selectedIds = CurrentUser.businessOwner!!.business!!.featureIds
    private val selectedIdsPos = selectedIds?.map { name ->
        subCats.indexOfFirst { it.name == name }
    }

    private fun changed(): Boolean {
        var changed = false
        val selected = selected()
        if (selectedIds?.size != selected.size) {
            changed = true
        } else {
            selectedIds.forEachIndexed { index, s ->
                val name = selected[index]
                if (name != s) {
                    changed = true
                }
            }
        }
        return changed
    }

    override fun viewCreated(bind: FragmentBusinessSubcategoriesBinding) {
        bind.apply {
            toolbar.title = getString(R.string.qulayliklar)
            toolbar.setUpBackButton(this@BusinessFeaturesFragment)
            recyclerView.apply {
                adapter = adapterS.apply {
                    selectedIdsPos?.forEach {
                        it?.let { selectedPositions.add(it) }
                    }
                    submitList(subCats)
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            saveButton.setOnClickListener {
                if (changed()) {
                    val selectedNames = selected()
                    CurrentUser.businessOwner!!.setFeatures(selectedNames)
                }
                findNavController().popBackStack()
            }
        }
    }
}