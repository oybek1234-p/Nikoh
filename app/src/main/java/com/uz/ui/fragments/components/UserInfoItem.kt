package com.uz.ui.fragments.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.uz.nikoh.R
import com.uz.nikoh.databinding.UserInfoItemBinding
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
                nameView.text = value?.name
                phoneView.text = PhoneUtils.formatPhoneNumber(value?.phone ?: "-")
                if (value?.photo.isNullOrEmpty()) {
                    photoView.setImageResource(R.drawable.user_icon_placeholder)
                } else {

                }
            }
        }

    init {
        setPadding(toDp(18f), toDp(8f), toDp(18f), toDp(12f))
        setBackgroundRipple()
    }

}