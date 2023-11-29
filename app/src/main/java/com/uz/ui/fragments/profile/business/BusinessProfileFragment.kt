package com.uz.ui.fragments.profile.business

import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessProfileBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.components.set
import com.uz.ui.fragments.components.setText

class BusinessProfileFragment : BaseFragment<FragmentBusinessProfileBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_profile

    init {
        showBottomNav = true
    }

    override fun viewCreated(bind: FragmentBusinessProfileBinding) {
        bind.apply {
            businessHeader.setText(getString(R.string.biznes))
            adminHeader.titleView.text = getString(R.string.admin)

            CurrentUser.businessOwner?.businessLive?.observe(viewLifecycleOwner) {
                if (it != null) {
                    businessItem.apply {
                        set(it)
                    }
                }
            }
            businessItem.photoView.setOnClickListener {
                    navigate(R.id.businessPhotosFragment)
            }
            CurrentUser.userLive.observe(viewLifecycleOwner) {
                if (it != null) {
                    userItem.user = it
                }
            }
            userItem.setOnClickListener {
                navigate(R.id.editClientProfileFragment)
            }
            businessItem.root.setOnClickListener {
                navigate(R.id.businessSettingsFragment)
            }
            buttonSettings.setOnClickListener {
                navigate(R.id.settingsFragment)
            }

            buttonHelp.setOnClickListener {

            }
        }
    }


}