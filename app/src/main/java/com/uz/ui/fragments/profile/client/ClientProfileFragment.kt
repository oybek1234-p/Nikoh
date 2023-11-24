package com.uz.ui.fragments.profile.client

import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentClientProfileBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment

class ClientProfileFragment : BaseFragment<FragmentClientProfileBinding>() {

    override val layId: Int
        get() = R.layout.fragment_client_profile

    init {
        showBottomNav = true
    }

    override fun viewCreated(bind: FragmentClientProfileBinding) {
        bind.apply {
            headerTitle.titleView.text = getString(R.string.akkaunt)
            CurrentUser.user.let {
                userItem.user = it
            }
            userItem.setOnClickListener {
                navigate(R.id.action_clientProfileFragment_to_editClientProfileFragment)
            }
            buttonSettings.apply {
                title = context.getString(R.string.sozlamalar)
                setOnClickListener {

                }
            }
            buttonHelp.apply {
                title = context.getString(R.string.yordam)
                setOnClickListener {

                }
            }
            businessChangeButton.apply {
                title = context.getString(R.string.biznes_akk_otish)
                setOnClickListener {

                }
            }
        }
    }
}