package com.eexposito.kickstarterdashboard.helpers

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import org.jetbrains.anko.inputMethodManager

fun Drawable.setColorResId(context: Context, @ColorRes colorResId: Int) = setColorFilter(
    ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_IN
)

fun ImageView.setColorResId(@ColorRes colorResId: Int) = setColorFilter(
    ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_IN
)

fun Activity.forceSoftKeyboardHiding() {
    inputMethodManager.hideSoftInputFromWindow(
        if (currentFocus == null) {
            bind<View>(android.R.id.content).value.windowToken
        } else {
            currentFocus!!.windowToken
        },
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
}

fun <T : View> View.bind(@IdRes res: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
}
