package com.uz.ui.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.postDelayed
import com.google.android.material.color.MaterialColors
import com.uz.base.exception.ExceptionHandler
import com.uz.nikoh.appContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun showToast(message: String) {
    MainScope().launch {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }
}

private fun showKeyboard(view: View?) {
    view?.postDelayed(0) {
        try {
            if (view is EditText) {
                view.requestFocus()
            }
            val inputManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            ExceptionHandler.handle(e)
        }
    }
}

fun EditText.showKeyboard() {
    showKeyboard(this)
}

fun hideSoftInput(activity: Activity) {
    try {
        val manager =
            appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    } catch (e: java.lang.Exception) {
        ExceptionHandler.handle(e)
    }
}

fun toDp(value: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, appContext.resources.displayMetrics
    ).toInt()
}

fun Context.getMaterialColor(colorId: Int) =
    MaterialColors.getColor(
        this,
        colorId,
        0
    )