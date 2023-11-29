package com.uz.ui.fragments.profile.business

import android.os.Bundle
import com.uz.nikoh.R
import com.uz.nikoh.business.allPhotosCount
import com.uz.nikoh.category.Categories
import com.uz.nikoh.databinding.FragmentBusinessSettingsBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.category.CategoryFragment

class BusinessSettingsFragment : BaseFragment<FragmentBusinessSettingsBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_settings

    override fun viewCreated(bind: FragmentBusinessSettingsBinding) {
        bind.apply {
            this.toolbar.setUpBackButton(this@BusinessSettingsFragment)
            CurrentUser.businessOwner?.business?.apply {
                toolbar.title = name
                imagesButton.subtitle =
                    allPhotosCount().toString() + " ${getString(R.string.rasmlar)}"
                categoryButton.subtitle = Categories.getCategory(categoryId)?.name ?: ""

                nameAboutButton.setOnClickListener {
                    navigate(R.id.businessNameAndAboutFragment)
                }
                imagesButton.setOnClickListener {
                    navigate(R.id.businessPhotosFragment)
                }
                videoButton.setOnClickListener {
                    navigate(R.id.businessVideoFragment)
                }
                mediaLinkButton.setOnClickListener {
                    navigate(R.id.businessMediaLinksFragment)
                }
                categoryButton.setOnClickListener {
                    navigate(R.id.categoryFragment, Bundle().apply {
                        putString(CategoryFragment.SELECTED_ID, categoryId)
                    })
                }
                locationButton.setOnClickListener {
                    navigate(R.id.businessLocationFragment)
                }
            }
        }
    }
}