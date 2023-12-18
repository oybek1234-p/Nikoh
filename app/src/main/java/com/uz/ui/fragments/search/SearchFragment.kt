package com.uz.ui.fragments.search

import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.business.Business
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentSearchBinding
import com.uz.nikoh.databinding.SearchItemBinding
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.components.setBackgroundRipple
import com.uz.ui.utils.showKeyboard
import com.uz.ui.utils.visibleOrGone

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val layId: Int
        get() = R.layout.fragment_search

    private val viewModel = SearchViewModel()

    private val adapterR =
        object : BaseAdapter<Business, SearchItemBinding>(R.layout.search_item, EmptyDiffUtil()) {
            override fun bind(holder: ViewHolder<*>, model: Business, pos: Int) {
                holder.binding.apply {
                    (this as SearchItemBinding)
                    if (root.background !is RippleDrawable) {
                        root.setBackgroundRipple()
                    }
                    textView.text = model.name
                    subtitleView.text = Categories.getCategory(model.categoryId)?.name
                }
            }
        }.apply {
            addClickListener(object : ListClickListener<Business>() {
                override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: Business) {
                    openSearchResults(model.name)
                }
            })
        }

    fun openSearchResults(text: String) {
        navigate(R.id.searchResultsFragment, Bundle().apply {
            putString(SearchResultsFragment.SEARCH_TEXT, text)
        })
    }

    override fun onResume() {
        super.onResume()
        binding?.searchView?.showKeyboard()
    }

    override fun viewCreated(bind: FragmentSearchBinding) {
        bind.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            searchView.addTextChangedListener { it ->
                viewModel.search(it.toString())
            }
            searchView.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = searchView.text?.toString()
                    if (searchText.isNullOrEmpty().not()) {
                        //Open search result fragment
                        openSearchResults(searchText!!)
                    } else {
                        searchView.requestFocus()
                    }
                }
                return@setOnEditorActionListener true
            }
            viewModel.apply {
                loading.observe(viewLifecycleOwner) {
                    progressBar.visibleOrGone(it)
                }
                empty.observe(viewLifecycleOwner) {
                    emptyView.visibleOrGone(it)
                }
                searchList.observe(viewLifecycleOwner) {
                    adapterR.submitList(it)
                }
            }
            recyclerView.apply {
                adapter = adapterR
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
    }


}