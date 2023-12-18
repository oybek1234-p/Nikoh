package com.uz.nikoh.user

import com.google.firebase.database.DatabaseReference
import com.uz.base.data.firebase.DataResult

interface UserControllerInterface {

    suspend fun loadUser(userId: String): DataResult<User>
    fun getUser(userId: String): User?
    fun userExistsCache(userId: String): Boolean
    fun updateLastSeen(userId: String) {}
    fun userReference(userId: String): DatabaseReference
    fun updateUser(user: User, done: (success: Boolean, exc: Exception?) -> Unit)
}