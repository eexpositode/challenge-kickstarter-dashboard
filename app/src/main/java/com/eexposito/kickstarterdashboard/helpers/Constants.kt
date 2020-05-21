package com.eexposito.kickstarterdashboard.helpers

import android.content.Context
import androidx.annotation.StringRes
import com.android.volley.VolleyError
import com.eexposito.kickstarterdashboard.R
import com.squareup.moshi.JsonDataException

const val API_URL = "http://starlord.hackerearth.com/kickstarter"
const val PROJECT_URL_PREFIX = "https://www.kickstarter.com"

sealed class AppException(
    private val error: Throwable? = null,
    @StringRes val titleResId: Int? = null,
    @StringRes private val messageResId: Int? = null
) : Throwable() {

    object ProjectListIsNullException : AppException(
        messageResId = R.string.project_list_is_null_exception
    )

    data class VolleyException(
        val error: VolleyError
    ) : AppException(error = error, titleResId = R.string.json_parsing_error_title)

    data class JSONParseException(
        val error: JsonDataException
    ) : AppException(error = error, titleResId = R.string.json_parsing_error_title)

    data class UnknownException(val error: Throwable) : AppException(error = error)

    fun getErrorMessage(context: Context) = messageResId?.let { context.getString(messageResId) }
        ?: error?.localizedMessage
        ?: context.getString(R.string.unknown_error_message)
}

fun Throwable.toAppException() = when (this) {
    is AppException -> this
    is JsonDataException -> AppException.JSONParseException(
        this
    )
    is VolleyError -> AppException.VolleyException(
        this
    )
    else -> AppException.UnknownException(
        this
    )
}
