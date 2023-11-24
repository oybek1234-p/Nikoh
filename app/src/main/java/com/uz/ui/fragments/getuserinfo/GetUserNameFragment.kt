package com.uz.ui.fragments.getuserinfo

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentUserNameBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment
import com.uz.ui.utils.showKeyboard

class GetUserNameFragment : BaseFragment<FragmentUserNameBinding>() {
    override val layId: Int
        get() = R.layout.fragment_user_name

    private var backEnabled = false

    companion object {
        const val BACK_ENABLED = "back"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backEnabled = arguments?.getBoolean(BACK_ENABLED) ?: false
    }

    private fun nameInput() = binding?.nameView?.editText?.text?.toString()

    private fun nameValid() = (nameInput()?.length ?: 0) > 2

    override fun onResume() {
        super.onResume()
        binding?.nameView?.editText?.showKeyboard()
    }

    private var set = false

    override fun viewCreated(bind: FragmentUserNameBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@GetUserNameFragment)
            toolbar.showArrowBack = backEnabled

            nameView.editText?.addTextChangedListener {
                continueButton.isEnabled = nameValid()
            }

            continueButton.setOnClickListener {
                if (set) return@setOnClickListener
                set = true
                CurrentUser.setName(nameInput()!!)
                if (backEnabled) {
                    findNavController().popBackStack()
                } else {
                    mainActivity()?.showBaseScreen()
                }
            }
        }
    }
}