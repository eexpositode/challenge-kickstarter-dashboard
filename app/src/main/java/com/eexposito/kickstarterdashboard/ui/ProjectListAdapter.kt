package com.eexposito.kickstarterdashboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import kotlinx.android.synthetic.main.project_item_view.view.*

/**
 * [RecyclerView.Adapter] that can display a [ProjectItem] and makes a call to the
 * specified [OnProjectItemInteractionListener].
 */
class ProjectListAdapter(
    private val listener: ProjectListView.OnProjectItemInteractionListener?
) : RecyclerView.Adapter<ProjectListAdapter.ProjectItemViewHolder>() {

    private var values = mutableListOf<ProjectItem>()
    private val mOnClickListener = View.OnClickListener { v ->
        (v.tag as? ProjectItem)?.let { listener?.onProjectItemInteraction(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.project_item_view, parent, false)
    )

    override fun onBindViewHolder(holder: ProjectItemViewHolder, position: Int) {
        holder.setUp(values[position], mOnClickListener)
    }

    override fun getItemCount(): Int = values.size

    fun updateData(updatedValues: List<ProjectItem>) {
        values.clear()
        values.addAll(updatedValues)
        notifyDataSetChanged()
    }

    inner class ProjectItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setUp(item: ProjectItem, listener: View.OnClickListener) {
            view.apply {
                projectTitle.text = item.title
                projectPledge.text = item.pledge
                projectBackers.text = item.backers
                projectDaysLeft.text = item.daysLeft
                tag = item
                setOnClickListener(listener)
            }
        }
    }
}
