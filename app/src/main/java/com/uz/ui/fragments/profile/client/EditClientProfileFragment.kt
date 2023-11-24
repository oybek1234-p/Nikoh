package com.uz.ui.fragments.profile.client

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentEditProfileClientBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.base.BaseFragment

class EditClientProfileFragment : BaseFragment<FragmentEditProfileClientBinding>() {
    
    override val layId: Int
        get() = R.layout.fragment_edit_profile_client

    private fun isNameValid() = (binding?.nameView?.editText?.text?.toString()?.length ?: 0) > 3

    override fun viewCreated(bind: FragmentEditProfileClientBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@EditClientProfileFragment)

            CurrentUser.userLive.observe(viewLifecycleOwner) { us ->
                nameView.editText?.setText(us.name)
                photoView.apply {
                    if (us.photo.isNotEmpty()) {

                    } else {
                        setImageResource(R.drawable.user_icon_placeholder)
                    }
                }
                phoneView.editText?.setText(PhoneUtils.formatPhoneNumber(us.phone))
            }
            phoneView.editText?.apply {
                isFocusable = false
                isClickable = false
                isEnabled = false
            }
            nameView.editText?.addTextChangedListener {
                saveButton.isEnabled = isNameValid()
            }
            saveButton.setOnClickListener {
                var changed = false
                val name = nameView.editText?.text?.toString() ?: return@setOnClickListener
                if (name != CurrentUser.user.name) {
                    changed = true
                }
                if (changed) {
                    CurrentUser.setName(name)
                }
                findNavController().popBackStack()
            }
        }

    }
}