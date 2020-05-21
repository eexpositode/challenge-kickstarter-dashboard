package com.eexposito.kickstarterdashboard.helpers

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Drawable.setColorResId(context: Context, @ColorRes colorResId: Int) = setColorFilter(
    ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_IN
)

fun ImageView.setColorResId(@ColorRes colorResId: Int) = setColorFilter(
    ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_IN
)