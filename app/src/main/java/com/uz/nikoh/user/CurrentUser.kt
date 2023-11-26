package com.uz.nikoh.user

import androidx.lifecycle.MutableLiveData
import com.uz.base.data.firebase.firebaseAuth
import com.uz.base.data.firebase.justResult
import com.uz.base.imagekit.ImageUploader
import com.uz.nikoh.utils.TimeUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object CurrentUser {

    private val config = UserConfig()
    val user: User get() = config.user
    val userLive = MutableLiveData(user)
    val photoUploading = MutableLiveData(false)

    fun userLogged() = user.id.isNotEmpty()

    private fun userReference() = UserController.userReference(user.id)

    private fun updateUser(
        newUser: User = user,
        network: Boolean = true,
        done: ((success: Boolean) -> Unit)? = null
    ) {
        config.user = newUser
        config.saveUser()
        userLive.postValue(newUser)
        if (network) {
            userReference().setValue(newUser).apply {
                done?.let { justResult(it) }
            }
        } else {
            done?.invoke(true)
        }
    }

    private fun setPhotoTemp(path: String) {
        user.photo = path
        userLive.postValue(user)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setPhoto(path: String) {
        if (userLogged().not()) return
        setPhotoTemp(path)

        photoUploading.postValue(true)
        GlobalScope.launch {
            val uploaded = ImageUploader.uploadImage(path).await()
            photoUploading.postValue(false)
            uploaded.data?.let {
                setPhoto(it.url)
                user.photo = it.url
                updateUser(network = false)
                userReference().updateChildren(mapOf(Pair(User::photo.name, it.url)))
            }
        }
    }

    fun setName(name: String) {
        if (userLogged().not()) return
        user.name = name
        updateUser(network = false)
        userReference().updateChildren(mapOf(Pair(User::name.name, name)))
    }

    fun applyFirebaseUser(result: (user: User?) -> Unit) {
        val firebaseUser = firebaseAuth().currentUser ?: return
        if (firebaseUser.phoneNumber.isNullOrEmpty()) return
        val uid = firebaseUser.uid

        UserController.loadUser(uid) {
            var user = it.data
            val exists = user != null
            if (user == null) {
                user = User().apply {
                    id = uid
                    phone = firebaseUser.phoneNumber!!
                    createdAt = TimeUtils.currentTime()
                }
            }
            updateUser(user, network = exists.not()) {
                result.invoke(user)
            }
        }
    }
}