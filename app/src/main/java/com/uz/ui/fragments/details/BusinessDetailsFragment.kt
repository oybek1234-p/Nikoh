package com.uz.ui.fragments.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.uz.base.imagekit.ImageData
import com.uz.nikoh.R
import com.uz.nikoh.business.Business
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.Product
import com.uz.nikoh.business.SocialMedia
import com.uz.nikoh.business.category
import com.uz.nikoh.business.city
import com.uz.nikoh.category.BusinessFeature
import com.uz.nikoh.category.SubCategory
import com.uz.nikoh.category.getString
import com.uz.nikoh.databinding.BusinessDetailsFragmentBinding
import com.uz.nikoh.databinding.MediaLinkItemBinding
import com.uz.nikoh.price.priceText
import com.uz.nikoh.user.toUser
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.base.BaseAdapter
import com.uz.ui.base.BaseFragment
import com.uz.ui.base.ListClickListener
import com.uz.ui.fragments.location.LocationUtils
import com.uz.ui.fragments.photo.PhotoShow
import com.uz.ui.fragments.product.BusinessProductsFragment
import com.uz.ui.fragments.profile.business.settings.PhotosAdapter
import com.uz.ui.utils.LinkUtils
import com.uz.ui.utils.makeExpandable
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BusinessDetailsFragment : BaseFragment<BusinessDetailsFragmentBinding>() {
    private var businessId: String? = null
    private var business: Business? = null

    companion object {
        const val BUSINESS_ID = "bId"
    }

    private val photoAdapter = PhotosAdapter(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        businessId = arguments?.getString(BUSINESS_ID)?.also {
            business = BusinessController.Business.getBusiness(it)
        }
    }

    override val layId: Int
        get() = R.layout.business_details_fragment

    private val productsAdapter = BusinessProductsFragment.ProductAdapter(true).apply {
        addClickListener(object : ListClickListener<Product>() {
            override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: Product) {
                navigate(R.id.viewProductFragment, Bundle().apply {
                    putString(Product::id.name, model.id)
                })
            }
        })
    }

    private fun initProducts() {
        binding?.apply {
            if (business!!.productsCount == 0) {
                productsRecycler.visibleOrGone(false)
                tovarHeader.visibleOrGone(false)
                productsLoading.visibleOrGone(false)
                return
            }
            productsRecycler.adapter = productsAdapter
            val ownerId = business!!.admin!!.id
            tovarHeader.setOnClickListener {
                navigate(R.id.businessProductsFragment, Bundle().apply {
                    putBoolean(BusinessProductsFragment.IS_EDIT, false)
                    putString(BusinessProductsFragment.OWNER_ID, ownerId)
                })
            }
            val products = BusinessController.Products.getProducts(ownerId)
            if (products.isEmpty()) {
                productsLoading.visibleOrGone(true)
                lifecycleScope.launch {
                    val productsResult = BusinessController.Products.loadProducts(ownerId)
                    lifecycleScope.launch(Dispatchers.Main) {
                        productsLoading.visibleOrGone(false)
                        if (productsResult.data.isNullOrEmpty()) {
                            tovarHeader.visibleOrGone(false)
                            productsRecycler.visibleOrGone(false)
                        } else {
                            productsAdapter.submitList(productsResult.data)
                        }
                    }
                }
            } else {
                tovarHeader.visibleOrGone(products.isNotEmpty())
                productsLoading.visibleOrGone(false)
                productsAdapter.submitList(products)
            }
        }
    }

    private fun businessLoading(loading: Boolean) {
        binding?.apply {
            val showUi = loading.not()
            val viewGroup = root as ViewGroup
            viewGroup.children.forEach {
                if (it !is AppBarLayout) {
                    it.visibleOrGone(showUi)
                }
            }
            progressBar.visibleOrGone(loading)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun viewCreated(bind: BusinessDetailsFragmentBinding) {
        bind.apply {
            if (businessId.isNullOrEmpty()) return
            if (business == null) {
                businessLoading(true)
                lifecycleScope.launch {
                    business = BusinessController.Business.loadBusiness(businessId!!)
                    launch(Dispatchers.Main) {
                        businessLoading(false)
                        viewCreated(bind)
                    }
                }
                return
            }
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            if (business == null) return
            ViewCompat.requestApplyInsets(coordinator)
            business?.apply {
                viewPager.adapter = photoAdapter.apply {
                    val allPhotos = arrayListOf(photoMain!!).also { it.addAll(photos) }
                    submitList(allPhotos)
                    addClickListener(object : ListClickListener<ImageData>() {
                        override fun onClick(holder: BaseAdapter.ViewHolder<*>, model: ImageData) {
                            PhotoShow.showPhotos(this@BusinessDetailsFragment, allPhotos, model)
                        }
                    })
                }
                dotsIndicator.attachTo(viewPager)
                titleView.text = name
                subtitleView.text = "${city()?.resIdName?.getString()} - ${category()?.name}"
                aboutView.text = about
                aboutView.visibleOrGone(about.isNotEmpty())
                aboutView.makeExpandable()

//                videoHeader.visibleOrGone(videoUrl.isNotEmpty())
//                if (videoUrl.isNotEmpty()) {
//                    val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
//                        .autoplay(0)
//                        .build()
//                    lifecycle.addObserver(youTubeView)
//                    youTubeView.initialize(
//                        object : AbstractYouTubePlayerListener() {
//                            override fun onReady(youTubePlayer: YouTubePlayer) {
//                                youTubePlayer.cueVideo(YouTubeUtils.getYouTubeId(videoUrl) ?: "", 0f)
//                            }
//                        }, iFramePlayerOptions
//                    )
//                } else {
//                    val parent = youTubeView.parent as ViewGroup
//                    parent.removeView(youTubeView)
//                }
                connectUserButton.text =
                    "${getString(R.string.bog_lanish)} ${PhoneUtils.formatPhoneNumber(business!!.admin!!.phone)}"
                connectUserButton.setOnClickListener {
                    PhoneUtils.openCallPhone(requireActivity(), business!!.admin!!.phone)
                }
                subCategoryIds?.forEach { sub ->
                    val chip = Chip(requireContext()).apply {
                        checkedIcon = null
                        isCheckable = false
                        text = SubCategory.valueOf(sub).nameResId.getString()
                    }
                    subCatsContainer.addView(chip)
                }
                userLayout.user = admin?.toUser()?.also {
                    it.phone = getString(R.string.admin)
                }
                userLayout.setOnClickListener {

                }
                val photosCount = photos.size + 1
                viewPager.setPageTransformer { page, position ->
                    photoCountView.text = "${viewPager.currentItem + 1}/$photosCount"
                }
                priceView.text = price?.price?.priceText()
                priceTypeView.text = price?.priceType?.stringId?.getString()
                priceTypeView.visibleOrGone(price != null)
                priceView.visibleOrGone(price != null)
                featureIds?.forEach { fe ->
                    val chip = Chip(requireContext()).apply {
                        checkedIcon = null
                        isCheckable = false
                        text = BusinessFeature.valueOf(fe).nameResId.getString()
                    }
                    featuresContainer.addView(chip)
                }
                initProducts()
                mediaLinkContainer.apply {
                    var hasMedia = false
                    if (telegramLink.isNullOrEmpty().not()) {
                        hasMedia = true
                        MediaLinkItemBinding.inflate(layoutInflater, root as ViewGroup, false)
                            .apply {
                                addView(this.root)
                                iconView.setImageResource(R.drawable.telegram_ic)
                                titleView.text = SocialMedia.telegramUserName(telegramLink!!)
                                root.setOnClickListener {
                                    LinkUtils.openUrl(
                                        requireActivity(),
                                        SocialMedia.parseTelegramLink(telegramLink!!)
                                    )
                                }
                            }
                    }
                    if (instagramLink.isNullOrEmpty().not()) {
                        hasMedia = true
                        MediaLinkItemBinding.inflate(layoutInflater, root as ViewGroup, false)
                            .apply {
                                addView(this.root)
                                iconView.setImageResource(R.drawable.instagram_ic)
                                titleView.text = SocialMedia.instagramUserName(instagramLink!!)
                                root.setOnClickListener {
                                    LinkUtils.openUrl(
                                        requireActivity(), instagramLink!!
                                    )
                                }
                            }
                    }
                    visibleOrGone(hasMedia)
                    locationLayout.apply {
                        location?.apply {
                            titleView.text = city()?.resIdName?.getString()
                            subtitleView.text = address
                            mapButton.setOnClickListener {
                                latLng?.let {
                                    LocationUtils.openLatLng(requireActivity(), it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}