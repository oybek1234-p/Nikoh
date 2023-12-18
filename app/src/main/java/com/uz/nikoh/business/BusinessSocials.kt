package com.uz.nikoh.business

import android.content.Intent
import android.net.Uri
import com.uz.nikoh.R
import com.uz.nikoh.appContext

class BusinessSocials(
    var telegramLink: String? = null,
    var instagramLink: String? = null
)

open class SocialMedia {

    companion object {
        private const val TELEGRAM_SUFFIX = "https://t.me/"
        private const val INSTAGRAM_SUFFIX = "https://www.instagram.com/"
        private const val INSTAGRAM_SUFFIX2 = "https://instagram.com/"
        private const val TELEGRAM = "Telegram"
        private const val INSTAGRAM = "Instagram"

        fun openLink(link: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            appContext.startActivity(intent)
        }

        fun getIconForSocialMedia(link: String): Int {
            return if (isTelegramLink(link)) {
                R.drawable.telegram_ic
            } else if (isInstagramLink(link)) {
                R.drawable.instagram_ic
            } else {
                -1
            }
        }

        fun isTelegramLink(link: String?): Boolean {
            if (link == null) return false
            return link.startsWith(TELEGRAM_SUFFIX) || link.startsWith("@")
        }

        fun isInstagramLink(link: String?): Boolean {
            if (link == null) return false
            return link.startsWith(INSTAGRAM_SUFFIX) || link.startsWith(INSTAGRAM_SUFFIX2)
        }

        fun telegramUserName(link: String): String {
            if (link.startsWith(TELEGRAM_SUFFIX)) {
                return link.removePrefix(TELEGRAM_SUFFIX)
            } else if (link.startsWith("@")) {
                return link
            }
            return ""
        }

        fun instagramUserName(link: String): String {
            val slashIndex = link.lastIndexOf("/")
            val questionIndex = link.indexOf("?")
            return "@${link.substring(slashIndex + 1, questionIndex)}"
        }

        fun parseTelegramLink(link: String): String {
            return if (link.startsWith(TELEGRAM_SUFFIX)) {
                link
            } else {
                val l = link.removePrefix("@")
                "$TELEGRAM_SUFFIX$l"
            }
        }
    }
}