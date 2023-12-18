package com.uz.ui.fragments.profile.business

import android.os.Bundle
import com.uz.nikoh.R
import com.uz.nikoh.business.allPhotosText
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentBusinessSettingsBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.category.CategoryFragment
import com.uz.ui.fragments.product.BusinessProductsFragment

class BusinessSettingsFragment : BaseFragment<FragmentBusinessSettingsBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_settings

    override fun viewCreated(bind: FragmentBusinessSettingsBinding) {
        bind.apply {
            this.toolbar.setUpBackButton(this@BusinessSettingsFragment)
            CurrentUser.businessOwner?.business?.apply {
                toolbar.title = name
                imagesButton.subtitle = allPhotosText()
                categoryButton.subtitle = Categories.getCategory(categoryId)?.name ?: ""

                nameAboutButton.setOnClickListener {
                    navigate(R.id.businessNameAndAboutFragment)
                }
                nameAboutButton.subtitle =
                    "$name - ${about.ifEmpty { getString(R.string.malumot_qo_shish) }}"
                imagesButton.setOnClickListener {
                    navigate(R.id.businessPhotosFragment)
                }
                videoButton.setOnClickListener {
                    navigate(R.id.businessVideoFragment)
                }
                videoButton.subtitle =
                    videoUrl.ifEmpty { getString(R.string.you_tube_link_qo_shish) }
                mediaLinkButton.setOnClickListener {
                    navigate(R.id.businessMediaLinksFragment)
                }
                mediaLinkButton.subtitle = "Instagram, Telegram, YouTube"
                categoryButton.setOnClickListener {
                    navigate(R.id.categoryFragment, Bundle().apply {
                        putString(CategoryFragment.SELECTED_ID, categoryId)
                    })
                }
                locationButton.setOnClickListener {
                    navigate(R.id.businessLocationFragment)
                }
                productsButton.setOnClickListener {
                    navigate(R.id.businessProductsFragment, Bundle().apply {
                        putString(BusinessProductsFragment.OWNER_ID, CurrentUser.user.id)
                        putBoolean(BusinessProductsFragment.IS_EDIT, true)
                    })
                }
                locationButton.subtitle = location?.address ?: ""
                subCatButton.setOnClickListener {
                    navigate(R.id.businessSubcategoriesFragment)
                }
                featuresButton.setOnClickListener {
                    navigate(R.id.businessFeaturesFragment)
                }
                priceButton.setOnClickListener {
                    navigate(R.id.businessPriceSetFragment)
                }
            }
        }
    }
}