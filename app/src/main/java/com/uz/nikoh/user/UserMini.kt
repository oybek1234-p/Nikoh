package com.uz.nikoh.user

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

fun User.mini() = UserMini(id, name, photo, phone, business = isBusiness)