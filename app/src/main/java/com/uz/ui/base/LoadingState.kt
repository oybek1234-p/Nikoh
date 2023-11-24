package com.uz.ui.base

sealed class LoadingState {
    data object None : LoadingState()
    data object Loading : LoadingState()
    class Error(val exception: Exception?) : LoadingState()
    data object Success : LoadingState()
}