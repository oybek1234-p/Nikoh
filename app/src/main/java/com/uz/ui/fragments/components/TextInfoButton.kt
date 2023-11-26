package com.uz.ui.fragments.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.uz.nikoh.databinding.ButtonInfoBinding
import com.uz.ui.utils.visibleOrGone

class TextInfoButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defS: Int = 0
) :
    FrameLayout(context, attrs, defS) {

    private val binding = ButtonInfoBinding.inflate(LayoutInflater.from(context), this, true)
    var title = ""
        set(value) {
            field = value
            binding.titleView.text = value
        }

    var subtitle = ""
        set(value) {
            field = value
            binding.subtitleView.apply {
                visibleOrGone(value.isEmpty().not())
                text = value
            }
        }

    var iconRes = 0
        set(value) {
            field = value
            if (value != 0) {
                binding.iconView.apply {
                    visibleOrGone(true)
                    setImageResource(value)
                }
            }
        }

    var checkboxEnabled = false
        set(value) {
            field = value
            binding.checkBox.visibleOrGone(value)
            binding.arrowView.visibleOrGone(false)
        }

    var isCheckBoxChecked = false
        set(value) {
            field = value
            if (checkboxEnabled.not()) return
            binding.checkBox.isChecked = value
        }

    var arrowEnabled = false
        set(value) {
            field = value
            binding.arrowView.visibleOrGone(value)
        }

    init {
        setBackgroundRipple()
        setPadding(Dps.DP_18.value, Dps.DP_12.value, Dps.DP_18.value, Dps.DP_12.value)
    }

}
