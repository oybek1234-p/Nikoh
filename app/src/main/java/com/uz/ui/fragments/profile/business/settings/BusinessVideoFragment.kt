package com.uz.ui.fragments.profile.business.settings

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessVideoBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment

class BusinessVideoFragment : BaseFragment<FragmentBusinessVideoBinding>() {

    override val layId: Int
        get() = R.layout.fragment_business_video

    private val videoLinkText: String? get() = binding?.editView?.editText?.text?.toString()
    private fun canSave() = videoLinkText.isNullOrEmpty().not()

    override fun viewCreated(bind: FragmentBusinessVideoBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessVideoFragment)
            editView.editText?.setText(CurrentUser.businessOwner!!.business!!.videoUrl)
            editView.editText?.addTextChangedListener {
                saveButton.isEnabled = canSave()
            }
            saveButton.isEnabled = canSave()
            saveButton.setOnClickListener {
                if (canSave()) {
                    val videoUrl = videoLinkText!!
                    CurrentUser.businessOwner!!.setVideoUrl(videoUrl)
                    findNavController().popBackStack()
                }
            }
        }
    }
}