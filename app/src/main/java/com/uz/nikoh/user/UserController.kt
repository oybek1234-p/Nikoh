package com.uz.nikoh.user

import com.google.firebase.database.DatabaseReference
import com.uz.base.data.firebase.DataResult
import com.uz.base.data.firebase.databaseReference
import com.uz.nikoh.utils.TimeUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object UserController : UserControllerInterface {

    private const val USERS_PATH = "users"
    private val usersReference = databaseReference(USERS_PATH)
    private val usersMap = hashMapOf<String, User>()

    override fun userReference(userId: String): DatabaseReference {
        return usersReference.child(userId)
    }

    override suspend fun loadUser(userId: String) = suspendCoroutine { c ->
        if (userId.isEmpty()) return@suspendCoroutine
        usersReference.child(userId).get().addOnCompleteListener { it ->
            val user = it.result?.getValue(User::class.java)
            val result = DataResult(user, it.isSuccessful, it.exception)
            user?.let {
                usersMap[userId] = user
            }
            c.resume(result)
        }
    }

    override fun getUser(userId: String): User? {
        return usersMap[userId]
    }

    override fun userExistsCache(userId: String): Boolean {
        return getUser(userId) != null
    }

    override fun updateLastSeen(userId: String) {
        if (userId.isEmpty()) return
        usersReference.child(userId)
            .updateChildren(mapOf(Pair(User::lastSeenAt.name, TimeUtils.currentTime())))
    }

    override fun updateUser(user: User, done: (success: Boolean, exc: Exception?) -> Unit) {
        if (user.id.isEmpty()) return
        usersMap[user.id] = user
        usersReference.child(user.id).setValue(user).addOnCompleteListener {
            done.invoke(it.isSuccessful, it.exception)
        }
    }
}


