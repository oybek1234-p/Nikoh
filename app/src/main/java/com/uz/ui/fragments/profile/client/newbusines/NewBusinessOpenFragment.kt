package com.uz.ui.fragments.profile.client.newbusines

import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentChangeToBusinessBinding
import com.uz.ui.base.BaseFragment

class NewBusinessOpenFragment : BaseFragment<FragmentChangeToBusinessBinding>() {

    override val layId: Int
        get() = R.layout.fragment_change_to_business

    override fun viewCreated(bind: FragmentChangeToBusinessBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@NewBusinessOpenFragment)

            continueButton.setOnClickListener {
                navigate(R.id.newBusinessFragment)
            }
        }
    }


}