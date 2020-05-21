package com.eexposito.kickstarterdashboard.ui.delegates.search

import android.content.ComponentName
import androidx.appcompat.widget.SearchView
import kotlin.reflect.full.cast

interface SearchHostComponent {
    var searchRequestCallbacks: MutableList<SearchMediatorComponent>

    fun registerSearchViewDelegate(
        searchView: SearchView, componentName: ComponentName, searchHintResId: Int
    )

    fun unregisterSearchViewDelegate()

    fun doOnSearchResultSelected()

    fun shouldConsumeBackPressedEvent() : Boolean

    fun doOnStartSearchHostComponent(callback: SearchMediatorComponent) {
        searchRequestCallbacks.add(callback)
    }

    fun doOnStopSearchHostComponent(callback: SearchMediatorComponent) {
        searchRequestCallbacks.remove(callback)
    }

    fun onSearchRequested(query: String) {
        searchRequestCallbacks.forEach { it.onSearchRequested(query) }
    }
}

interface SearchMediatorComponent {

    var searchHostComponent: SearchHostComponent
    var searchClientComponent: SearchClientComponent

    fun declareSearchHostComponent(component: Any) = SearchHostComponent::class.cast(component)

    fun declareSearchClientComponent(component: Any) = SearchClientComponent::class.cast(component)

    fun doOnStartSearchHostComponent() {
        searchHostComponent.doOnStartSearchHostComponent(this)
    }

    fun doOnStopSearchHostComponent() {
        searchHostComponent.doOnStopSearchHostComponent(this)
    }

    fun selectSearchResult() {
        searchHostComponent.doOnSearchResultSelected()
    }

    fun onSearchRequested(query: String) {
        searchClientComponent.onSearchRequested(query)
    }
}

interface SearchClientComponent {
    fun onSearchRequested(query: String)
}
