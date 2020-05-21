package com.eexposito.kickstarterdashboard.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_input_int_range.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class IntRangePickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    interface OnFilterActionInteractionListener {
        fun onFilterActionClick(from: Int?, to: Int?)
        fun onVisibilityChange(isVisible: Boolean)
    }

    private var onFilterActionInteractionListener: OnFilterActionInteractionListener? = null

    fun bind(listener: OnFilterActionInteractionListener) {
        onFilterActionInteractionListener = listener
        filterActionButton.onClick {
            if (fromInputText.toIntOrNull() == null && toInputText.toIntOrNull() == null)
                return@onClick
            onFilterActionInteractionListener?.onFilterActionClick(
                fromInputText.toIntOrNull(), toInputText.toIntOrNull()
            )
        }
    }

    fun switchVisibility() {
        if (isVisible) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            fromInputText.text = null
            toInputText.text = null
        }
        onFilterActionInteractionListener?.onVisibilityChange(isVisible)
    }

    private fun EditText.toIntOrNull() = text.toString().toIntOrNull()
}
