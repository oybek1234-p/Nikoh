package com.uz.nikoh.business

import androidx.annotation.FloatRange

/**
 * Business rating
 * rating ranges 0.0 - 5.0
 */
data class BusinessRating(var isTop: Boolean, @FloatRange(from = 0.0, to = 5.0) var rating: Float)