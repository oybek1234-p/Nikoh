package com.uz.ui.fragments.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.color.MaterialColors

fun View.setBackgroundRipple() {
    background =
        RippleDrawable(
            ColorStateList.valueOf(
                MaterialColors.getColor(
                    context,
                    com.google.android.material.R.attr.colorSurfaceVariant,
                    null
                )
            ), null, ColorDrawable(Color.GRAY)
        )
}