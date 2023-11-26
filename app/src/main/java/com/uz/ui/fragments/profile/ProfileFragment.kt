package com.uz.ui.fragments.profile

import android.widget.FrameLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentProfileBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.components.InfoBanner

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val layId: Int
        get() = R.layout.fragment_profile

    private lateinit var navController: NavController

    init {
        showBottomNav = true
    }

    override fun viewCreated(bind: FragmentProfileBinding) {
        bind.apply {
            navController =
                (childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            if (CurrentUser.userLogged().not()) {
                val banner = InfoBanner(requireContext()).apply {
                    setInfoData(InfoBanner.bannerAuthNeeded) {
                        navigate(R.id.auth_graph)
                    }
                }
                (root as FrameLayout).addView(banner)
            } else {
                val graph = if (CurrentUser.user.isBusiness) {
                    R.navigation.business_profile_nav_graph
                } else {
                    R.navigation.client_profile_nav_graph
                }
                navController.setGraph(graph)
            }
        }
    }
}