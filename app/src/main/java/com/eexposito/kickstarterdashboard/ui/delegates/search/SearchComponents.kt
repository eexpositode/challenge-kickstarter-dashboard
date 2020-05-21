package com.eexposito.kickstarterdashboard.ui.delegates.search

import android.content.ComponentName
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import com.eexposito.kickstarterdashboard.helpers.AppException

abstract class SearchHostMediatorActivity :
    AppCompatActivity(),
    SearchHostComponent by SearchHostImpl(),
    SearchMediatorComponent by SearchMediatorImpl() {

    override fun onStart() {
        super.onStart()
        searchHostComponent = declareSearchHostComponent(this)
        searchClientComponent = declareSearchClientComponent(
            findViewById<View>(android.R.id.content).resolveSearchClientComponent()
                ?: throw AppException.ConstraintViolationException("Search client component not found")
        )
        doOnStartSearchHostComponent()
    }

    /**
     * BFS (Breadth first search) recursive method to find the first view to be a [SearchClientComponent]
     */
    private fun View.resolveSearchClientComponent(): SearchClientComponent? = when (this) {
        is SearchClientComponent -> this
        is ViewGroup -> children.map { it.resolveSearchClientComponent() }
            .firstOrNull { it is SearchClientComponent }
        else -> null
    }

    override fun onStop() {
        doOnStopSearchHostComponent()
        super.onStop()
    }

    override fun onSearchRequested(query: String) {
        super<SearchHostComponent>.onSearchRequested(query)
    }
}

class SearchHostImpl : SearchHostComponent {

    override var searchRequestCallbacks = mutableListOf<SearchMediatorComponent>()
    private var searchViewDelegate: SearchViewDelegate? = null

    override fun registerSearchViewDelegate(
        searchView: SearchView, componentName: ComponentName, searchHintResId: Int
    ) {
        searchViewDelegate = SearchViewDelegate(this, searchView, componentName, searchHintResId)
    }

    override fun unregisterSearchViewDelegate() {
        searchViewDelegate?.onPause()
    }

    override fun doOnSearchResultSelected() {
        searchViewDelegate?.doOnSearchResultSelected()
    }

    override fun shouldConsumeBackPressedEvent() =
        searchViewDelegate?.shouldConsumeBackPressedEvent() ?: false
}

class SearchMediatorImpl : SearchMediatorComponent {

    override lateinit var searchHostComponent: SearchHostComponent
    override lateinit var searchClientComponent: SearchClientComponent
}
