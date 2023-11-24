package com.uz.base.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.uz.base.exception.ExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KMutableProperty

fun fireStoreInstance() = FirebaseFirestore.getInstance()
fun firebaseDatabase() = FirebaseDatabase.getInstance()
fun firebaseStorage() = FirebaseStorage.getInstance()
fun firebaseAuth() = FirebaseAuth.getInstance()

fun fireStoreCollection(name: String) = fireStoreInstance().collection(name)
fun databaseReference(name: String) = firebaseDatabase().getReference(name)
fun storageReference(name: String) = firebaseStorage().getReference(name)

fun CollectionReference.deleteDocument(docId: String) = document(docId).delete()

fun hasFirebaseUser() = firebaseAuth().currentUser != null

fun <T : Any> Task<T>.justResult(result: (isSuccess: Boolean) -> Unit) {
    addOnCompleteListener {
        result(it.isSuccessful)
        if (exception != null) {
            ExceptionHandler.handle(exception)
        }
    }
}

data class DataResult<T>(val data: T?, val success: Boolean, val exception: Exception? = null)

suspend fun <T> Query.get(
    response: (result: DataResult<List<T>>) -> Unit,
    clazz: Class<T>
) = coroutineScope {
    get().addOnCompleteListener {
        launch {
            val list = it.result.toObjectsType(clazz)
            response(DataResult(list, it.isSuccessful, it.exception))
        }
    }
}

suspend fun <T> QuerySnapshot.toObjectsType(type: Class<T>): List<T> =
    withContext(Dispatchers.Default) {
        toObjects(type)
    }

fun <R> CollectionReference.update(
    id: String,
    field: KMutableProperty<R>,
    value: Any,
    result: (isSuccess: Boolean) -> Unit = {}
) {
    if (id.isEmpty()) return
    document(id).update(field.name, value).justResult(result)
}

fun <R> DocumentReference.update(
    field: KMutableProperty<R>,
    value: Any,
    result: (isSuccess: Boolean) -> Unit = {}
) {
    update(field.name, value).justResult(result)
}

private val increaseField = FieldValue.increment(1)
private val decreaseField = FieldValue.increment(-1)
private val increaseFieldRealtime = ServerValue.increment(1)
private val decreaseFieldRealtime = ServerValue.increment(-1)

fun <T : Any> DocumentReference.increaseField(
    property: KMutableProperty<T>,
    increase: Boolean,
    result: (isSuccess: Boolean) -> Unit = {}
) {
    update(property.name, if (increase) increaseField else decreaseField).justResult(result)
}

fun getIncreaseValue(value: Long) = FieldValue.increment(value)

fun getIncreaseValueRealtime(value: Long) = ServerValue.increment(value)

fun increaseValue(increase: Boolean) = if (increase) increaseField else decreaseField

fun <R> DocumentReference.increase(field: KMutableProperty<R>, increase: Boolean) {
    update(field.name, increaseValue(increase))
}

fun <R> DatabaseReference.increase(field: KMutableProperty<R>, increase: Boolean) {
    child(field.name).setValue(if (increase) increaseFieldRealtime else decreaseFieldRealtime)
}
