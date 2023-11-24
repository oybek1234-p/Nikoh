package com.uz.ui.fragments.open

import com.uz.nikoh.R
import com.uz.nikoh.databinding.OpenScreenBinding
import com.uz.ui.base.BaseFragment

class OpenFragment : BaseFragment<OpenScreenBinding>() {

    override val layId: Int
        get() = R.layout.open_screen

    override fun viewCreated(bind: OpenScreenBinding) {
        bind.apply {
            skipButton.setOnClickListener {
                mainActivity()?.showBaseScreen()
            }
            authButton.setOnClickListener {
                navigate(
                    R.id.action_openFragment_to_auth_graph,
                    null
                )
            }
        }
    }
}