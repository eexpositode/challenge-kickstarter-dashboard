package com.eexposito.kickstarterdashboard.helpers

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.eexposito.kickstarterdashboard.R

fun createInfoDialog(context: Context, titleId: Int?, messageId: Int, positiveAction: () -> Unit = {}) =
    createInfoDialog(
        context,
        titleId,
        context.getString(messageId),
        positiveAction
    )

fun createInfoDialog(context: Context, titleId: Int?, message: String, positiveAction: () -> Unit = {}) =
    AlertDialog.Builder(context).apply {
        if (titleId != null) setTitle(getContext().getString(titleId))
        setMessage(message)
        setCancelable(false)
        setPositiveButton(getContext().getString(R.string.dialog_positive_button)) { dialog, _ ->
            positiveAction()
            dialog.cancel()
        }
    }.create()
