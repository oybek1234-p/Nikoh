package com.uz.ui.fragments.profile.business.settings

import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessLocationBinding
import com.uz.nikoh.location.addressWithCity
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.location.MapFragment
import com.uz.ui.utils.visibleOrGone

class BusinessLocationFragment : BaseFragment<FragmentBusinessLocationBinding>() {
    override val layId: Int
        get() = R.layout.fragment_business_location

    override fun viewCreated(bind: FragmentBusinessLocationBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessLocationFragment)

            CurrentUser.businessOwner!!.businessLive.observe(viewLifecycleOwner) {
                val address = it?.location?.address
                addressView.apply {
                    visibleOrGone(address != null)
                    text = it?.location?.addressWithCity()
                }
            }
            addLocationButton.setOnClickListener {
                MapFragment.launch(this@BusinessLocationFragment) {
                    val newLocation = it ?: return@launch
                    CurrentUser.businessOwner!!.setLocation(newLocation)
                }
            }
        }
    }


}