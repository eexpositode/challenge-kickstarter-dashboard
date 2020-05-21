package com.eexposito.kickstarterdashboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import kotlinx.android.synthetic.main.view_project_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [ProjectItem] and makes a call to the
 * specified [OnProjectItemInteractionListener].
 */
class ProjectListAdapter(
    private val listener: ProjectListView.OnProjectItemInteractionListener?
) : RecyclerView.Adapter<ProjectListAdapter.ProjectItemViewHolder>() {

    private val filterTitleDelegate =
        FilterDelegate<ProjectItem, ProjectListAdapter>(this) { item, searchString ->
            item.title.contains(searchString, ignoreCase = true)
        }

    private val mOnClickListener = View.OnClickListener { v ->
        (v.tag as? ProjectItem)?.let { listener?.onProjectItemInteraction(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_project_item, parent, false)
    )

    override fun onBindViewHolder(holder: ProjectItemViewHolder, position: Int) {
        holder.setUp(filterTitleDelegate.filteredItemList[position], mOnClickListener)
    }

    override fun getItemCount(): Int = filterTitleDelegate.filteredItemList.size

    fun updateData(
        updatedValues: List<ProjectItem>, onListUpdated: (List<ProjectItem>) -> Unit = {}
    ) {
        filterTitleDelegate.updateList(updatedValues, onListUpdated)
    }

    fun filter(textFilter: String?) {
        filterTitleDelegate.filter.filter(textFilter)
    }

    inner class ProjectItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setUp(item: ProjectItem, listener: View.OnClickListener) {
            view.apply {
                projectTitle.text = item.title
                projectPledge.text = item.pledge
                projectBackers.text = item.backersToDisplay
                projectDaysLeft.text = item.daysLeft
                tag = item
                setOnClickListener(listener)
            }
        }
    }
}

class FilterDelegate<T, A : RecyclerView.Adapter<*>>(
    private val adapter: A, private val filterPredicate: (item: T, searchString: String) -> Boolean
) : Filterable {
    var originalItemList = mutableListOf<T>()
    var filteredItemList = mutableListOf<T>()
    var searchString = ""

    fun updateList(viewItemList: List<T>, onListUpdated: (List<T>) -> Unit) {
        originalItemList = viewItemList.toMutableList()
        filteredItemList.clear()
        filteredItemList.addAll(filterDataList(originalItemList, searchString))
        adapter.notifyDataSetChanged()
        onListUpdated(filteredItemList)
    }

    private fun filterDataList(dataList: List<T>, searchString: String) =
        if (searchString.isEmpty()) dataList
        else dataList.filter { filterPredicate(it, searchString) }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence) = FilterResults().apply {
            searchString = constraint.toString()
            if (searchString.isBlank()) {
                values = originalItemList
                count = originalItemList.size
            } else {
                originalItemList
                    .filter { filterPredicate(it, searchString.trimStart(' ').trimEnd(' ')) }
                    .let { filteredList ->
                        values = filteredList
                        count = filteredList.size
                    }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredItemList = (results.values as? List<T>)?.toMutableList() ?: mutableListOf()
            adapter.notifyDataSetChanged()
        }
    }
}
