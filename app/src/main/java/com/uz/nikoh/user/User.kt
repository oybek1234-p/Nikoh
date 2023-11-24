package com.uz.nikoh.user

import com.uz.nikoh.location.LocationData

/**
 * User class, there is two type of users, customer user and business owner
 * isBusiness - if it is business owner
 */
class User {

    var id = ""
    var name = ""
    var photo = ""
    var phone = ""
    var location: LocationData? = null

    var lastSeenAt = 0L
    var createdAt = 0L

    var savedCount = 0

    var isBusiness = false
}