package com.uz.ui.fragments.profile.business.settings

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentBusinessMediaLinksBinding
import com.uz.nikoh.user.CurrentUser
import com.uz.ui.base.BaseFragment

class BusinessMediaLinksFragment : BaseFragment<FragmentBusinessMediaLinksBinding>() {

    override val layId: Int
        get() = R.layout.fragment_business_media_links


    private fun instagramText() = binding?.instagramView?.editText?.text?.toString()
    private fun telegramText() = binding?.telegramView?.editText?.text?.toString()
    private fun youTubeText() = binding?.youtubeView?.editText?.text?.toString()

    private fun canSave() =
        instagramText().isNullOrEmpty().not() || telegramText().isNullOrEmpty()
            .not() || youTubeText().isNullOrEmpty().not()

    override fun viewCreated(bind: FragmentBusinessMediaLinksBinding) {
        bind.apply {
            toolbar.setUpBackButton(this@BusinessMediaLinksFragment)
            val updateSave = {
                saveButton.isEnabled = canSave()
            }
            CurrentUser.businessOwner!!.business?.apply {
                instagramView.editText?.setText(instagramLink)
                youtubeView.editText?.setText(youTubeLink)
                telegramView.editText?.setText(telegramLink)
            }
            instagramView.editText?.addTextChangedListener {
                updateSave.invoke()
            }
            telegramView.editText?.addTextChangedListener {
                updateSave.invoke()
            }
            youtubeView.editText?.addTextChangedListener {
                updateSave.invoke()
            }
            updateSave.invoke()
            saveButton.setOnClickListener {
                val instagram = instagramText()
                val telegram = telegramText()
                val youTube = youTubeText()
                CurrentUser.businessOwner!!.setMediaLinks(telegram, instagram, youTube)
                findNavController().popBackStack()
            }
        }
    }
}