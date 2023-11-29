package com.uz.nikoh.category

import com.uz.nikoh.R

enum class BusinessFeature(
    val nameResId: Int,
    val photoResId: Int,
    val subtitleResId: Int = -1
) {
    WIFI(R.string.wifi, R.drawable.help_ic),
}