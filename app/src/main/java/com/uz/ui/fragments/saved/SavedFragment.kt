package com.uz.ui.fragments.saved

import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentSavedBinding
import com.uz.ui.base.BaseFragment

class SavedFragment : BaseFragment<FragmentSavedBinding>() {

    override val layId: Int
        get() = R.layout.fragment_saved

    init {
        showBottomNav = true
    }

    override fun viewCreated(bind: FragmentSavedBinding) {
        bind.apply {

        }
    }
}