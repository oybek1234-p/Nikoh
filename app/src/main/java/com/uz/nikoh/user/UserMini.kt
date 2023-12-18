package com.uz.nikoh.user

import kotlinx.serialization.Serializable

@Serializable
class UserMini {
    var id = ""
    var name = ""
    var photo = ""
    var phone = ""
    var isBusiness = false

    constructor()

    constructor(id: String, name: String, photo: String, phone: String, business: Boolean) {
        this.id = id
        this.name = name
        this.phone = phone
        this.photo = photo
        this.isBusiness = business
    }
}

fun UserMini.toUser() = User().apply {
    this.id = this@toUser.id
    this.name = this@toUser.name
    this.phone = this@toUser.phone
    this.photo = this@toUser.photo
    this.isBusiness = this@toUser.isBusiness
}

fun User.mini() = UserMini(id, name, photo, phone, business = isBusiness)