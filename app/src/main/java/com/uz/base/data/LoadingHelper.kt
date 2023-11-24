package com.uz.base.data

import com.uz.base.data.firebase.DataResult

class LoadingHelper<T> {

    private var loadingIds = mutableSetOf<String>()
    private var loadingCallbacks = mutableMapOf<String, ArrayList<LoadCallback<T>>>()

    private fun isLoading(id: String) = loadingIds.contains(id)

    private fun removeLoading(id: String) {
        loadingIds.remove(id)
        loadingCallbacks[id]?.clear()
    }

    private fun setLoading(id: String) {
        loadingIds.add(id)
    }

    private fun addCallback(id: String, loadCallback: LoadCallback<T>) {
        loadingCallbacks.getOrPut(id) { arrayListOf() }.add(loadCallback)
    }

    fun postResult(id: String, result: DataResult<T>) {
        removeLoading(id)
        loadingCallbacks[id]?.apply {
            forEach {
                it.invoke(result)
            }
            clear()
        }

    }

    fun observeLoad(id: String, done: LoadCallback<T>, load: LoadingHelper<T>.() -> Unit): Boolean {
        if (isLoading(id)) {
            addCallback(id, done)
            return true
        } else {
            setLoading(id)
            load.invoke(this)
        }
        return false
    }
}

typealias LoadCallback<T> = (DataResult<T>) -> Unit