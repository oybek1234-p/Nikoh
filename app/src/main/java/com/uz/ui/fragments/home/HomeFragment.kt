package com.uz.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentHomeBinding
import com.uz.nikoh.location.LocationHelper
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.category.CategoryFragment
import com.uz.ui.fragments.search.SearchResultsFragment
import com.uz.ui.utils.update

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layId: Int
        get() = R.layout.fragment_home

    private val viewModel: HomeViewModel by viewModels()

    private fun initList() {
        viewModel.feedList.apply {
            add(
                FeedAdapter.FeedModel(
                    FeedAdapter.VIEW_TYPE_HEADER,
                    FeedAdapter.Header(appContext.getString(R.string.kategoriyalar), {
                        setFragmentResultListener(CategoryFragment.SELECT) { _, b ->
                            val selectId = b.getString(CategoryFragment.SELECTED_ID)
                            navigate(R.id.searchResultsFragment, Bundle().apply {
                                putString(SearchResultsFragment.CATEGORY_NAME, selectId)
                            })
                        }
                        navigate(R.id.categoryFragment, Bundle().apply {
                            putBoolean(CategoryFragment.SELECT, true)
                        })
                    }, true)
                )
            )
            add(
                FeedAdapter.FeedModel(
                    FeedAdapter.VIEW_TYPE_CATEGORIES, Categories.categories
                )
            )
            viewModel.feedListLive.update()
        }
    }

    init {
        showBottomNav = true
    }

    private val adapterR = FeedAdapter(this) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initList()
    }

    override fun viewCreated(bind: FragmentHomeBinding) {
        bind.apply {
            viewModel.apply {
                feedListLive.observe(viewLifecycleOwner) {
                    adapterR.submitList(it)
                }
            }
            LocationHelper.searchLocationLive.observe(viewLifecycleOwner) {
                cityView.addressView.text =
                    getString(LocationHelper.getSearchLocationCity().resIdName)
            }
            searchBar.setOnClickListener {
                navigate(R.id.searchFragment)
            }
            cityView.root.setOnClickListener {
                navigate(R.id.chooseSearchLocationFragment)
            }
            recyclerView.apply {
                adapter = adapterR
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            mainActivity()?.requestSearchLocation()
        }
    }

}