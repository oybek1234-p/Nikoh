package com.uz.nikoh.category

import com.uz.nikoh.R
import com.uz.nikoh.appContext
import com.uz.nikoh.category.Categories.CatsId.GULLAR

object Categories {

    enum class CatsId {
        OFORMLENIYA, ZAKS, QOSHIQCHI, KARTEJ, BOSHLOVCHI, KUYLAK, GULLAR, VIDEO, FOTO, RESTORAN, HOSTES, KVARTET, RAQQOSLAR, MILLIY, OFISIANT, DJ, POVAR, TORT, KULGU, PROKAT, MONITOR, OVOZ, SARPO_SANDIQ, KARVIN
    }

    private fun getString(id: Int) = appContext.getString(id)

    private val subCategories = mapOf(
        Pair(
            CatsId.RESTORAN.name,
            listOf(
                SubCategory.MarryMe,
                SubCategory.LoveStory,
                SubCategory.NaxorgiOsh,
                SubCategory.OilaviyFotosesiya,
                SubCategory.TuyKechasi
            )
        )
    )

    private val features = mapOf(
        Pair(
            CatsId.RESTORAN.name,
            listOf(
                BusinessFeature.HAMYONBOB_NARX,
                BusinessFeature.WIFI,
                BusinessFeature.KATTA_PARKOVKA
            )
        )
    )

    fun getFeatures(catId: String) = BusinessFeature.values().toList()
    fun getSubcategories(catId: String) = SubCategory.values().toList()

    val categories by lazy {
        listOf(
            Category(getString(R.string.oformleniya), R.string.oform_url, CatsId.OFORMLENIYA, true),
            Category(getString(R.string.qo_shiqchi), R.string.qosh_url, CatsId.QOSHIQCHI, false),
            Category(getString(R.string.restoran), R.string.restoran_url, CatsId.RESTORAN,false),
            Category(getString(R.string.boshlovchi), R.string.boshlovchi_url, CatsId.BOSHLOVCHI,false),
            Category(getString(R.string.kartej), R.string.kartej_url, CatsId.KARTEJ,true),
            Category(getString(R.string.kuylak), R.string.kuylak_url, CatsId.KUYLAK,true),
            Category(getString(R.string.gullar), R.string.gullar_url, GULLAR,true),
            Category(getString(R.string.sarpo), R.string.sarpo_url, CatsId.SARPO_SANDIQ,true),
            Category(getString(R.string.raqqoslar), R.string.raqqos_url, CatsId.RAQQOSLAR,false),
            Category(getString(R.string.karvin), R.string.karvin_url, CatsId.KARVIN,true),
            Category(getString(R.string.zaks), R.string.zaks_url, CatsId.ZAKS,false),
            Category(getString(R.string.video), R.string.video_url, CatsId.VIDEO),
            Category(getString(R.string.foto), R.string.foto_url, CatsId.FOTO),
            Category(getString(R.string.hostes), R.string.hostes_url, CatsId.HOSTES),
            Category(getString(R.string.kvartet), R.string.kvartet_url, CatsId.KVARTET),
            Category(getString(R.string.milliy), R.string.milliy_url, CatsId.MILLIY),
            Category(getString(R.string.offisiant), R.string.ofisiant_url, CatsId.OFISIANT),
            Category(
                getString(R.string.dj), R.string.dj_url, CatsId.DJ
            ),
            Category(getString(R.string.povar), R.string.povar_url, CatsId.POVAR),
            Category(getString(R.string.tort), R.string.tort_url, CatsId.TORT),
            Category(
                getString(R.string.kulgu), R.string.kulgu_url, CatsId.KULGU
            ),
            Category(
                getString(R.string.prokat), R.string.prokat_url, CatsId.PROKAT
            ),
            Category(getString(R.string.monitor), R.string.monitor_url, CatsId.MONITOR),
            Category(
                getString(R.string.ovoz), R.string.ovoz_url, CatsId.OVOZ
            )
        )
    }

    fun getCategory(id: CatsId) = categories.find { it.catId == id }
    fun getCategory(nameId: String?) = categories.find { it.catId.name == nameId }
}