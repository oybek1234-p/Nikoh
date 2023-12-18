package com.uz.nikoh.category

import android.content.Context
import com.uz.nikoh.R
import com.uz.nikoh.appContext

enum class SubCategory(
    var nameResId: Int, var photoResId: Int = 0
) {
    KuyovNavkar(R.string.kuyov_navkar, -1),

    OilaviyFotosesiya(R.string.oilaviy_fotosisiya, -1),
    NaxorgiOsh(R.string.naxorgi_osh, -1),
    TuyKechasi(R.string.tuy_kechasi),
    LoveStory(R.string.love_story),
    MarryMe(R.string.marry_me)
}

fun Int.getString(context: Context = appContext) = context.getString(this)

