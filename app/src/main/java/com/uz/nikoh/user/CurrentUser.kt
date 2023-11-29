package com.uz.nikoh.user

import androidx.lifecycle.MutableLiveData
import com.uz.base.data.firebase.firebaseAuth
import com.uz.base.data.firebase.justResult
import com.uz.base.imagekit.ImageUploader
import com.uz.nikoh.business.Business
import com.uz.nikoh.business.BusinessController
import com.uz.nikoh.business.BusinessOwner
import com.uz.nikoh.utils.TimeUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CurrentUser {

    private val config = UserConfig()
    val user: User get() = config.user
    val userLive = MutableLiveData(user)
    val photoUploading = MutableLiveData(false)

    var businessOwner: BusinessOwner? = null

    init {
        if (user.isBusiness) {
            businessOwner = BusinessOwner()
        }
        updateFromNetwork()
    }

    private fun updateFromNetwork() {
        if (userLogged()) {
            UserController.loadUser(user.id) {
                if (it.success && it.data != null) {
                    updateUser(it.data, network = false)
                }
            }
        }
    }

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

    suspend fun applyFirebaseUser(result: (user: User?) -> Unit) = withContext(Dispatchers.Main) {
        val firebaseUser = firebaseAuth().currentUser ?: return@withContext
        if (firebaseUser.phoneNumber.isNullOrEmpty()) return@withContext
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
            val done = {
                updateUser(user, network = exists.not()) {
                    result.invoke(user)
                }
            }
            if (exists && user.isBusiness) {
                MainScope().launch {
                    val businessL = BusinessController.loadBusiness(user.id)
                    if (businessL != null) {
                        businessOwner = BusinessOwner().apply {
                            update(businessL, false)
                        }
                        done.invoke()
                    } else {
                        result.invoke(null)
                    }
                }
            } else {
                done.invoke()
            }
        }
    }

    fun changeToBusiness(name: String, catId: String, done: (success: Boolean) -> Unit) {
        user.isBusiness = true
        updateUser {
            if (it) {
                val newBusiness = Business().apply {
                    id = user.id
                    this.name = name
                    this.categoryId = catId
                    admin = user.mini()
                }
                businessOwner = BusinessOwner().apply {
                    update(newBusiness, true, done = done)
                }
            } else {
                user.isBusiness = false
                userLive.postValue(user)
                done.invoke(false)
            }
        }
    }

}