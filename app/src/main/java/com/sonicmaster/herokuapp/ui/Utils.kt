package com.sonicmaster.herokuapp.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.ui.auth.LoginFragment
import com.sonicmaster.herokuapp.ui.base.BaseFragment

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("Incorrect email or password")
            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        failure.errorCode == 404 -> {
            requireView().snackbar("Resource Not Found!")
        }
        failure.isNetworkError == true -> requireView().snackbar(
            "Please check your internet connection",
            retry
        )
        else -> {
            val error = failure.errorBody?.string()
            requireView().snackbar(error!!)
        }
    }
}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor.use {
        it?.moveToFirst()
        if (cursor != null) {
            if (it != null) {
                name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    return name
}