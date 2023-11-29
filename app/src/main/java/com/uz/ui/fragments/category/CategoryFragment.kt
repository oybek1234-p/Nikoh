package com.uz.ui.fragments.category

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.category.Categories
import com.uz.nikoh.category.Category
import com.uz.nikoh.databinding.CategoryItemBinding
import com.uz.nikoh.databinding.FragmentCategoriesBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.SelectableAdapter
import com.uz.ui.utils.getMaterialColor

class CategoryFragment : BaseFragment<FragmentCategoriesBinding>() {

    override val layId: Int
        get() = R.layout.fragment_categories

    var selectedId: String? = null
    var selectable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedId = arguments?.getString(SELECTED_ID)
        selectable = arguments?.getBoolean(SELECT) ?: false
    }

    companion object {
        const val SELECT = "select"
        const val SELECTED_ID = "selected"
    }

    private var firstCreate = true

    private val adapterCat = object : SelectableAdapter<Category, CategoryItemBinding>(
        false,
        R.layout.category_item,
        EmptyDiffUtil()
    ) {
        override fun bind(holder: ViewHolder<*>, model: Category, pos: Int, selected: Boolean) {
            holder.apply {
                binding.apply {
                    (this as CategoryItemBinding)
                    root.setBackgroundColor(
                        requireContext().getMaterialColor(
                            if (selected) com.google.android.material.R.attr.colorSecondaryContainer else com.google.android.material.R.attr.colorSurface,
                        )
                    )
                    titleView.setTextColor(
                        requireContext().getMaterialColor(
                            if (selected) com.google.android.material.R.attr.colorOnSecondaryContainer else com.google.android.material.R.attr.colorOnSurface,
                        )
                    )
                    photoView.loadUrl(getString(model.photoResId))
                    titleView.text = model.name
                }
            }
        }

        override fun onSelected(pos: Int, selected: Boolean) {
            if (selected && !firstCreate) {
                val category = getItem(pos)
                setFragmentResult(SELECT, Bundle().apply {
                    putString(SELECTED_ID, category.catId.name)
                })
                findNavController().popBackStack()
            }
        }
    }

    override fun viewCreated(bind: FragmentCategoriesBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@CategoryFragment)
            toolbar.title =
                if (selectable) getString(R.string.kategoriyani_tanlang) else getString(R.string.kategoriyalar)
            recyclerView.apply {
                adapter = adapterCat.apply {
                    this.selectable = this@CategoryFragment.selectable
                    submitList(Categories.categories)
                    if (selectedId.isNullOrEmpty().not()) {
                        val index =
                            Categories.categories.indexOfFirst { it.catId.name == selectedId }
                        setSelected(index)
                    }
                    firstCreate = false
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
    }

}