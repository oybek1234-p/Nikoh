package com.uz.nikoh.category

import com.uz.nikoh.R

enum class BusinessFeature(
    val nameResId: Int,
    val photoResId: Int = -1,
    val subtitleResId: Int = -1
) {
    WIFI(R.string.wifi, R.drawable.help_ic),
    KATTA_PARKOVKA(R.string.katta_parkovka),
    HAMYONBOB_NARX(R.string.hamayonbob_narx)
}