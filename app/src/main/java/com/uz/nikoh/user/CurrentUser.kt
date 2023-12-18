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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
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
        GlobalScope.launch {
            updateFromNetwork()
        }
    }

    private suspend fun updateFromNetwork() {
        if (userLogged()) {
            val result = UserController.loadUser(user.id)
            if (result.success && result.data != null) {
                updateUser(result.data, network = false)
            }
        }
    }

    fun userLogged() = user.id.isNotEmpty()

    private fun userReference() = UserController.userReference(user.id)

    fun signOut() {
        firebaseAuth().signOut()
        config.clear()
        businessOwner?.clear()
        businessOwner = null
    }

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
                businessOwner?.apply {
                    business?.admin?.photo = user.photo
                    updateAdmin()
                }
                updateUser(network = false)
                userReference().updateChildren(mapOf(Pair(User::photo.name, it.url)))
            }
        }
    }

    fun setName(name: String) {
        if (userLogged().not()) return
        user.name = name
        businessOwner?.apply {
            business?.admin?.name = name
            updateAdmin()
        }
        updateUser(network = false)
        userReference().updateChildren(mapOf(Pair(User::name.name, name)))
    }

    suspend fun applyFirebaseUser(
        debug: Boolean = false,
        debugPhone: String? = null,
        result: (user: User?) -> Unit
    ) = withContext(Dispatchers.Main) {
        val firebaseUser = firebaseAuth().currentUser
        if (debug.not() && firebaseUser == null) return@withContext
        if (debug && debugPhone.isNullOrEmpty()) return@withContext
        val uid = firebaseUser?.uid ?: debugPhone!!
        val userResult = UserController.loadUser(uid)
        userResult.let {
            var user = it.data
            val exists = user != null
            if (user == null) {
                user = User().apply {
                    id = uid
                    phone = firebaseUser?.phoneNumber ?: debugPhone ?: ""
                    createdAt = TimeUtils.currentTime()
                }
            }
            val done = {
                updateUser(user, network = exists.not()) {
                    result.invoke(user)
                }
            }
            if (exists && user.isBusiness) {
                launch(Dispatchers.Main) {
                    val businessL = BusinessController.Business.loadBusiness(user.id)

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