package com.uz.ui.fragments.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import com.uz.nikoh.R
import com.uz.nikoh.databinding.UserInfoItemBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.User
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.utils.toDp
import com.uz.ui.utils.visibleOrGone

class UserInfoItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defS: Int = 0
) :
    ConstraintLayout(context, attrs, defS) {

    private var binding = UserInfoItemBinding.inflate(LayoutInflater.from(context), this, true)

    var user: User? = null
        set(value) {
            field = value

            binding.apply {
                value?.apply {
                    nameView.text = name

                    phoneView.text = if (phone.isDigitsOnly()) PhoneUtils.formatPhoneNumber(
                        phone
                    ) else phone
                    if (photo == "no") {
                        photoView.visibleOrGone(false)
                    } else {
                        if (photo.isEmpty()) {
                            photoView.setImageResource(R.drawable.user_icon_placeholder)
                        } else {
                            photoView.loadUrl(photo)
                        }
                    }
                }
            }
        }

    init {
        setPadding(toDp(18f), toDp(8f), toDp(18f), toDp(12f))
        setBackgroundRipple()
    }

}