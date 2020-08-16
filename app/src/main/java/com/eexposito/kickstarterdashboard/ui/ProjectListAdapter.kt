package com.eexposito.kickstarterdashboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import kotlinx.android.synthetic.main.view_project_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [ProjectItem] and makes a call to the
 * specified [ProjectListView.OnProjectItemInteractionListener].
 */
class ProjectListAdapter(
    private val listener: ProjectListView.OnProjectItemInteractionListener?
) : RecyclerView.Adapter<ProjectListAdapter.ProjectItemViewHolder>() {

    private val projects = mutableListOf<ProjectItem>()

    private val mOnClickListener = View.OnClickListener { v ->
        (v.tag as? ProjectItem)?.let { listener?.onProjectItemInteraction(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.view_project_item, parent, false)
    )

    override fun onBindViewHolder(holder: ProjectItemViewHolder, position: Int) {
        holder.setUp(projects[position], mOnClickListener)
    }

    override fun getItemCount(): Int = projects.size

    fun updateData(
        updatedValues: List<ProjectItem>, onListUpdated: (List<ProjectItem>) -> Unit = {}
    ) {
        projects.clear()
        projects.addAll(updatedValues)
        notifyDataSetChanged()
        onListUpdated(projects)
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