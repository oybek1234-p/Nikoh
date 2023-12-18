package com.uz.nikoh.api

import com.google.firebase.database.DatabaseReference
import com.uz.base.data.firebase.DataResult
import com.uz.base.data.firebase.databaseReference
import com.uz.nikoh.user.User
import com.uz.nikoh.utils.TimeUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserApi {

    private const val USERS_PATH = "users"
    private val usersReference = databaseReference(USERS_PATH)

    fun userReference(userId: String): DatabaseReference {
        return usersReference.child(userId)
    }

    suspend fun loadUser(userId: String) = suspendCoroutine { c ->
        if (userId.isEmpty()) return@suspendCoroutine
        usersReference.child(userId).get().addOnCompleteListener { it ->
            val user = it.result?.getValue(User::class.java)
            val result = DataResult(user, it.isSuccessful, it.exception)
            c.resume(result)
        }
    }

    fun updateLastSeen(userId: String) {
        if (userId.isEmpty()) return
        usersReference.child(userId)
            .updateChildren(mapOf(Pair(User::lastSeenAt.name, TimeUtils.currentTime())))
    }

    fun updateUser(user: User, done: (success: Boolean, exc: Exception?) -> Unit) {
        if (user.id.isEmpty()) return
        usersReference.child(user.id).setValue(user).addOnCompleteListener {
            done.invoke(it.isSuccessful, it.exception)
        }
    }
    
}