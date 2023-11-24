package com.uz.ui.fragments.home

import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentHomeBinding
import com.uz.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layId: Int
        get() = R.layout.fragment_home

    init {
        showBottomNav = true
    }

    override fun viewCreated(bind: FragmentHomeBinding) {
        bind.root.setOnClickListener {
            navigate(R.id.action_homeFragment_to_auth_graph)
        }
    }

}