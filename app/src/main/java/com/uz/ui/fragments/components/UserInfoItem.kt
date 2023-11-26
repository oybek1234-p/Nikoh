package com.uz.ui.fragments.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.uz.nikoh.R
import com.uz.nikoh.databinding.UserInfoItemBinding
import com.uz.nikoh.photo.loadUrl
import com.uz.nikoh.user.User
import com.uz.nikoh.utils.PhoneUtils
import com.uz.ui.utils.toDp

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
                    phoneView.text = PhoneUtils.formatPhoneNumber(phone ?: "-")
                    if (photo.isEmpty()) {
                        photoView.setImageResource(R.drawable.user_icon_placeholder)
                    } else {
                        photoView.loadUrl(photo)
                    }
                }
            }
        }

    init {
        setPadding(toDp(18f), toDp(8f), toDp(18f), toDp(12f))
        setBackgroundRipple()
    }

}