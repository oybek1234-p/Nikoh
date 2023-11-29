package com.uz.ui.fragments.profile.business.settings

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentGetBusinessInfoBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment

class BusinessNameAndAboutFragment : BaseFragment<FragmentGetBusinessInfoBinding>() {

    override val layId: Int
        get() = R.layout.fragment_get_business_info

    private val nameText: String? get() = binding?.nameView?.editText?.text?.toString()
    private val aboutText: String? get() = binding?.aboutView?.editText?.text?.toString()
    private fun isValid() = nameText.isNullOrEmpty().not() && aboutText.isNullOrEmpty().not()

    override fun viewCreated(bind: FragmentGetBusinessInfoBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessNameAndAboutFragment)
            CurrentUser.businessOwner?.business?.let {
                nameView.editText?.setText(it.name)
                aboutView.editText?.setText(it.about)
            }
            nameView.editText?.addTextChangedListener {
                saveButton.isEnabled = isValid()
            }
            aboutView.editText?.addTextChangedListener {
                saveButton.isEnabled = isValid()
            }
            saveButton.isEnabled = isValid()
            saveButton.setOnClickListener {
                val business = CurrentUser.businessOwner?.business
                val changed = nameText != business?.name || aboutText != business?.about
                if (changed) {
                    CurrentUser.businessOwner?.setNameAndAbout(nameText!!, aboutText!!)
                }
                findNavController().popBackStack()
            }
        }
    }
}