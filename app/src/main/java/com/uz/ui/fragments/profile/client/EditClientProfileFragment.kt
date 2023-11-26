package com.uz.ui.fragments.profile.client

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentEditProfileClientBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.CurrentUser
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.base.BaseFragment
import com.uz.ui.fragments.photo.PhotoGetFragment
import com.uz.ui.utils.showToast
import com.uz.ui.utils.visibleOrGone

class EditClientProfileFragment : BaseFragment<FragmentEditProfileClientBinding>() {

    override val layId: Int
        get() = R.layout.fragment_edit_profile_client

    private fun isNameValid() = (binding?.nameView?.editText?.text?.toString()?.length ?: 0) > 3

    override fun onPhotoGetResult(paths: List<String>) {
        showToast(paths.size.toString())
    }

    override fun viewCreated(bind: FragmentEditProfileClientBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@EditClientProfileFragment)

            CurrentUser.userLive.observe(viewLifecycleOwner) { us ->
                nameView.editText?.setText(us.name)
                photoView.apply {
                    if (us.photo.isNotEmpty()) {
                        loadUrl(us.photo)
                    } else {
                        setImageResource(R.drawable.user_icon_placeholder)
                    }
                }
                phoneView.editText?.setText(PhoneUtils.formatPhoneNumber(us.phone))
            }
            CurrentUser.photoUploading.observe(viewLifecycleOwner) {
                progressPhotoView.visibleOrGone(it)
            }
            photoView.setOnClickListener {
                if (CurrentUser.photoUploading.value == true) return@setOnClickListener
                PhotoGetFragment.navigate(this@EditClientProfileFragment, false) {
                    val url = it.firstOrNull() ?: return@navigate
                    CurrentUser.setPhoto(url)
                }
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