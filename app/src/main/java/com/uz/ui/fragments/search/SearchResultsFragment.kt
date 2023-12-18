package com.uz.ui.fragments.search

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.category.Categories
import com.uz.nikoh.category.Category
import com.uz.nikoh.category.getString
import com.uz.nikoh.databinding.FragmentSearchResultsBinding
import com.uz.nikoh.location.LocationHelper
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.home.FeedAdapter
import com.uz.ui.utils.visibleOrGone

class SearchResultsFragment : BaseFragment<FragmentSearchResultsBinding>() {
    override val layId: Int
        get() = R.layout.fragment_search_results

    private var searchText = ""
    private var category: Category? = null
    private var byCategory = false

    private val viewModel: SearchResultsViewModel by viewModels()

    private val feedAdapter = FeedAdapter(this) {
        viewModel.loadList()
    }

    companion object {
        const val SEARCH_TEXT = ""
        const val CATEGORY_NAME = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchText = arguments?.getString(SEARCH_TEXT) ?: ""
        val categoryName = arguments?.getString(CATEGORY_NAME) ?: ""
        if (categoryName.isNotEmpty()) {
            category = Categories.getCategory(categoryName)
            byCategory = true
        }
    }

    var city = LocationHelper.getSearchLocationCity().name

    override fun viewCreated(bind: FragmentSearchResultsBinding) {
        bind.apply {
            ViewCompat.requestApplyInsets(root)
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            titleView.text = category?.name?.ifEmpty { searchText } ?: searchText
            titleView.setOnClickListener {
                findNavController().popBackStack()
            }
            locationButton.text = getString(LocationHelper.getSearchLocationCity().resIdName)
            locationButton.setOnClickListener {
                navigate(R.id.chooseSearchLocationFragment)
            }
            recyclerView.apply {
                adapter = feedAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            viewModel.apply {
                filterByButton.text = filter.stringRes.getString()
                setFragmentResultListener(SearchFilterFragment.FILTER_KEY) { _, b ->
                    val newFilter = BusinessController.Business.BusinessFilter.valueOf(
                        b.getString(SearchFilterFragment.FILTER_KEY) ?: ""
                    )
                    filter = newFilter
                    filterByButton.text = filter.stringRes.getString()
                    viewModel.reloadList()
                }
                filterByButton.setOnClickListener {
                    navigate(R.id.searchFilterFragment, Bundle().apply {
                        putString(SearchFilterFragment.FILTER_KEY, filter.name)
                    })
                }
                loading.observe(viewLifecycleOwner) {
                    val next = feedAdapter.currentList.isNotEmpty()
                    progressBar.visibleOrGone(next.not() && it)
                    feedAdapter.setLoading(next && it)
                }
                empty.observe(viewLifecycleOwner) {
                    emptyView.visibleOrGone(it)
                }
                searchListLive.observe(viewLifecycleOwner) { it ->
                    feedAdapter.submitList(it)
                }
                this.searchText = this@SearchResultsFragment.searchText
                this.categoryId = this@SearchResultsFragment.category?.catId?.name

                val locationCity = LocationHelper.getSearchLocationCity().name
                if (city != locationCity) {
                    city = locationCity
                    viewModel.reloadList()
                } else {
                    if (feedAdapter.currentList.size == 0) {
                        loadList()
                    }
                }
            }
        }
    }
}