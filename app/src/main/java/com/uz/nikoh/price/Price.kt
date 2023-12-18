package com.uz.nikoh.price

import com.uz.nikoh.appContext
import kotlinx.serialization.Serializable

@Serializable
data class Price(var currencyId: String, var price: Long) {
    constructor() : this(Currency.UZS.name, 0L)
}

fun Price.currency() = Currency.valueOf(currencyId)
fun Price.priceText() = "$price ${appContext.getString(Currency.valueOf(currencyId).secondaryId)}"