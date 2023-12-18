package com.uz.nikoh.price

import com.uz.nikoh.R

enum class Currency(val resId: Int, val secondaryId: Int = 0) {
    UZS(R.string.uzs, R.string.sum),
    USD(R.string.usd, R.string.dollar)
}