package com.eexposito.kickstarterdashboard.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import kotlinx.android.synthetic.main.project_list_view.view.*

class ProjectListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    interface OnProjectItemInteractionListener {
        fun onProjectItemInteraction(item: ProjectItem)
    }

    private var projectListAdapter: ProjectListAdapter? = null

    fun bind(listener: OnProjectItemInteractionListener) {
        ProjectListAdapter(listener).let {
            projectListAdapter = it
            projectRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = it
            }
        }
    }

    fun updateList(projectList: List<ProjectItem>) {
        if (projectListAdapter == null)
            throw RuntimeException("Bind needs to be called to instantiate the adapter")
        projectListAdapter!!.updateData(projectList)
    }
}
