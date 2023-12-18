package com.uz.nikoh.business

import com.uz.nikoh.R
import com.uz.nikoh.price.Price
import kotlinx.serialization.Serializable

@Serializable
class BusinessPrice(
    var priceType: PriceType, var price: Price
) {
    constructor() : this(PriceType.MIN_PRICE, Price())
}


enum class PriceType(val stringId: Int) {
    EXACT_PRICE(R.string.aniq_xizmat_narxi), MIN_PRICE(R.string.boshlangich_narx)
}