package com.uz.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.business.Business
import com.uz.nikoh.business.Product
import com.uz.nikoh.business.category
import com.uz.nikoh.category.Categories
import com.uz.nikoh.category.Category
import com.uz.nikoh.category.getString
import com.uz.nikoh.databinding.BusinessFeedItemBinding
import com.uz.nikoh.databinding.HeaderItemBinding
import com.uz.nikoh.databinding.HomeCategoryItemBinding
import com.uz.nikoh.databinding.HorizontalRecyclerViewBinding
import com.uz.nikoh.databinding.ProductFeedItemBinding
import com.uz.nikoh.location.addressWithCity
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.price.priceText
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.EmptyDiffUtil
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.details.BusinessDetailsFragment
import com.uz.ui.fragments.profile.business.settings.PhotosAdapter
import com.uz.ui.fragments.search.SearchResultsFragment
import com.uz.ui.utils.visibleOrGone

class FeedAdapter(val fragment: BaseFragment<*>, val loadNext: () -> Unit) :
    BaseAdapter<FeedAdapter.FeedModel, ViewDataBinding>(R.layout.header_item, EmptyDiffUtil()) {

    data class FeedModel(val type: Int, val data: Any)
    data class Header(
        val title: String, val onClick: (() -> Unit)? = null, val arrowEnabled: Boolean
    )

    private val progressModel = FeedModel(VIEW_TYPE_PROGRESS, Any())

    fun setLoading(loading: Boolean) {
        val current = currentList.toMutableList()
        current.remove(progressModel)
        if (loading) {
            current.add(progressModel)
        }
        submitList(current)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
    }

    companion object {
        var VIEW_TYPE_HEADER = R.layout.header_item
        var VIEW_TYPE_CATEGORIES = R.layout.horizontal_recycler_view
        var VIEW_TYPE_BUSINESS = R.layout.business_feed_item
        var VIEW_TYPE_PROGRESS = R.layout.feed_progress_bar
        val VIEW_TYPE_PRODUCT = R.layout.product_feed_item
    }

    override fun getLayoutId(position: Int): Int {
        return getItem(position).type
    }

    override fun onViewCreated(holder: ViewHolder<ViewDataBinding>, viewType: Int) {
        holder.binding.apply {
            when (viewType) {
                VIEW_TYPE_BUSINESS -> {
                    (this as BusinessFeedItemBinding)
                    val openDetails = {
                        fragment.navigate(R.id.businessDetailsFragment, Bundle().apply {
                            putString(
                                BusinessDetailsFragment.BUSINESS_ID,
                                (getItem(holder.adapterPosition).data as Business).id
                            )
                        })
                    }
                    photoViewPager.adapter = PhotosAdapter().apply {
                        addClickListener(object :
                            ListClickListener<com.uz.base.imagekit.ImageData>() {
                            override fun onClick(
                                holder: ViewHolder<*>, model: com.uz.base.imagekit.ImageData
                            ) {
                                openDetails.invoke()
                            }
                        })
                    }
                    dotsIndicator.attachTo(photoViewPager)
                    root.setOnClickListener {
                        openDetails.invoke()
                    }
                }

                VIEW_TYPE_PRODUCT -> {
                    (this as ProductFeedItemBinding)
                    val openDetails = {
                        fragment.navigate(R.id.businessDetailsFragment, Bundle().apply {
                            putString(
                                BusinessDetailsFragment.BUSINESS_ID,
                                (getItem(holder.adapterPosition).data as Product).ownerId
                            )
                        })
                    }
                    photoViewPager.adapter = PhotosAdapter().apply {
                        addClickListener(object :
                            ListClickListener<com.uz.base.imagekit.ImageData>() {
                            override fun onClick(
                                holder: ViewHolder<*>, model: com.uz.base.imagekit.ImageData
                            ) {
                                openDetails.invoke()
                            }
                        })
                    }
                    dotsIndicator.attachTo(photoViewPager)
                    root.setOnClickListener {
                        openDetails.invoke()
                    }
                }

                VIEW_TYPE_CATEGORIES -> {
                    val adapterC = object : BaseAdapter<Category, HomeCategoryItemBinding>(
                        R.layout.home_category_item, EmptyDiffUtil()
                    ) {
                        override fun bind(holder: ViewHolder<*>, model: Category, pos: Int) {
                            holder.binding.apply {
                                (this as HomeCategoryItemBinding)
                                photoView.loadUrl(appContext.getString(model.photoResId))
                                titleView.text = model.name
                            }
                        }
                    }.apply {
                        addClickListener(object : ListClickListener<Category>() {
                            override fun onClick(holder: ViewHolder<*>, model: Category) {
                                fragment.navigate(R.id.searchResultsFragment, Bundle().apply {
                                    putString(SearchResultsFragment.CATEGORY_NAME, model.catId.name)
                                })
                            }
                        })
                    }
                    (this as HorizontalRecyclerViewBinding)
                    recyclerView.apply {
                        adapter = adapterC
                        layoutManager = LinearLayoutManager(
                            holder.itemView.context, LinearLayoutManager.HORIZONTAL, false
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(holder: ViewHolder<*>, model: FeedModel, pos: Int) {
        val data = model.data
        if (pos == currentList.size - 1) {
            loadNext.invoke()
        }
        holder.binding.apply {
            when (model.type) {
                VIEW_TYPE_PRODUCT -> {
                    (this as ProductFeedItemBinding)
                    val business = model.data as Product
                    photoViewPager.adapter?.apply {
                        if (this is PhotosAdapter) {
                            submitList(business.photos)
                        }
                    }
                    titleView.text = business.name
                    val category = business.categoryId.let { Categories.getCategory(it) }
                    photoCountView.text = (business.photos.size + 1).toString()
                    subtitleView.text = business.location?.addressWithCity()
                    infoView.text = "${category?.name}  ${business.businessName}"
                    priceView.text = business.price?.priceText()
                    priceTypeView.visibleOrGone(false)
                    priceView.visibleOrGone(business.price != null)
                }

                VIEW_TYPE_BUSINESS -> {
                    (this as BusinessFeedItemBinding)
                    val business = model.data as Business
                    photoViewPager.adapter?.apply {
                        if (this is PhotosAdapter) {
                            submitList(arrayListOf(business.photoMain).apply { addAll(business.photos) })
                        }
                    }
                    titleView.text = business.name
                    val category = business.category()
                    photoCountView.text = (business.photosCount + 1).toString()
                    subtitleView.text = business.location?.addressWithCity()
                    infoView.text = "${category?.name}"
                    priceView.text = business.price?.price?.priceText()
                    priceTypeView.text = business.price?.priceType?.stringId?.getString()
                    priceTypeView.visibleOrGone(business.price != null)
                    priceView.visibleOrGone(business.price != null)
                }

                VIEW_TYPE_HEADER -> {
                    (this as HeaderItemBinding)
                    if (data is Header) {
                        titleView.text = data.title
                        arrowView.visibleOrGone(data.arrowEnabled)
                        data.onClick?.let {
                            root.setOnClickListener { data.onClick.invoke() }
                        }
                    }
                }

                VIEW_TYPE_CATEGORIES -> {
                    (this as HorizontalRecyclerViewBinding)
                    (data as List<Category>)
                    recyclerView.adapter.apply {
                        (this as BaseAdapter<Category, *>).submitList(data)
                    }
                }


            }
        }
    }
}