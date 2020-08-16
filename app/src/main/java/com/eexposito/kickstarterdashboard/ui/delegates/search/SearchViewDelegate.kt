package com.eexposito.kickstarterdashboard.ui.delegates.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.helpers.setColorResId
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.textColor

class SearchViewDelegate(
    private val componentName: ComponentName,
    private val hintResId: Int,
    private val onSearchRequested: (String) -> Unit,
    private val searchView: SearchView
) {

    init {
        searchView.apply {
            setOnQueryTextListener(null)
            setSearchableInfo(
                (context.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
                    .getSearchableInfo(componentName)
            )
            maxWidth = Integer.MAX_VALUE
            customize(hintResId)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = true

                override fun onQueryTextChange(query: String): Boolean {
                    onSearchRequested(query)
                    return false
                }
            })
        }
    }

    fun shouldConsumeBackPressedEvent() = with(searchView) {
        if (!isIconified) {
            isIconified = true
            true
        } else false
    }

    /**
     * Close soft keyboard which we might have opened for the client search
     */
    fun onPause() {
        searchView.clearFocus()
    }

    fun doOnSearchResultSelected() {
        with(searchView) {
            isIconified = true // Remove search text
            clearFocus() // Hides keyboard
            onActionViewCollapsed() // Collapses view
        }
    }

    private fun SearchView.customize(hintResId: Int) {
        findViewById<View>(androidx.appcompat.R.id.search_plate)?.apply {
            //Set search background color
            backgroundColor = ContextCompat.getColor(context, R.color.colorSurface)
            //Set autocomplete text and hint colors
            findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text).apply {
                hint = context.getString(hintResId)
                textColor = ContextCompat.getColor(context, R.color.colorOnBackground)
                hintTextColor = ContextCompat.getColor(context, R.color.colorSecondary)
                highlightColor = ContextCompat.getColor(context, R.color.colorSecondary)
            }
            //Set close icon color
            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setColorResId(
                R.color.colorOnBackground
            )
        }
    }
}
