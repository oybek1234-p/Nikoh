package com.uz.ui.fragments.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import coil.load
import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.databinding.InfoBannerBinding
import com.uz.ui.utils.visibleOrGone

class InfoBanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defS: Int = 0
) :
    ConstraintLayout(context, attrs, defS) {
    private var binding = InfoBannerBinding.inflate(LayoutInflater.from(context), this, true)

    data class InfoBannerData(
        val photo: Any,
        val title: String,
        val subtitle: String,
        val actionButtonText: String
    )

    companion object {
        val bannerAuthNeeded by lazy {
            InfoBannerData(
                R.drawable.user_icon_placeholder,
                appContext.getString(R.string.akauntga_kiring),
                appContext.getString(R.string.auth_placeholder_subtitle),
                appContext.getString(R.string.kirish)
            )
        }

        val emptySaved by lazy {
            InfoBannerData(
                R.drawable.empty_saved_icon,
                appContext.getString(R.string.hsb),
                appContext.getString(R.string.sssyk),
                ""
            )
        }
    }

    fun setInfoData(infoData: InfoBannerData, actionButtonClick: (v: View?) -> Unit = {}) {
        infoData.apply {
            setInfo(photo, title, subtitle, actionButtonText, actionButtonClick)
        }
    }

    private fun setInfo(
        photo: Any,
        title: String,
        subtitle: String,
        actionButtonText: String? = null,
        actionButtonClick: (v: View?) -> Unit = {}
    ) {
        binding.apply {
            photoView.load(photo)
            titleView.text = title
            aboutView.text = subtitle
            if (actionButtonText.isNullOrEmpty().not()) {
                setActionButton(actionButtonText!!, actionButtonClick)
            }

        }
    }

    private fun setActionButton(buttonName: String, click: (v: View?) -> Unit) {
        binding.actionButton.apply {
            text = buttonName
            setOnClickListener(click)
            visibleOrGone(true)
        }
    }

    init {
        setPadding(Dps.DP_28.value)
    }
}