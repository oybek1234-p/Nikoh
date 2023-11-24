package com.uz.nikoh.user

import com.google.firebase.database.DatabaseReference
import com.uz.base.data.LoadCallback
import com.uz.base.data.LoadingHelper
import com.uz.base.data.firebase.DataResult
import com.uz.base.data.firebase.databaseReference
import com.uz.nikoh.utils.TimeUtils

object UserController : UserControllerInterface {

    private const val USERS_PATH = "users"
    private val usersReference = databaseReference(USERS_PATH)
    private val usersMap = hashMapOf<String, User>()
    private val loadingHelper = LoadingHelper<User>()

    override fun userReference(userId: String): DatabaseReference {
        return usersReference.child(userId)
    }

    override fun loadUser(userId: String, done: LoadCallback<User>) {
        if (userId.isEmpty()) return
        loadingHelper.observeLoad(userId, done) {
            usersReference.child(userId).get().addOnCompleteListener {
                val user = it.result?.getValue(User::class.java)
                val result = DataResult(user, it.isSuccessful, it.exception)
                loadingHelper.postResult(userId, result)
                done.invoke(result)
            }
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


