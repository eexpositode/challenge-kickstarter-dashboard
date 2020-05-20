package com.eexposito.kickstarterdashboard.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewModel
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewState
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.api.Project
import com.eexposito.kickstarterdashboard.helpers.AppException
import com.eexposito.kickstarterdashboard.helpers.createInfoDialog
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), ProjectListView.OnProjectItemInteractionListener{

    private val projectListViewModel: ProjectListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)
        (projectListView as? ProjectListView)?.bind(this)
        projectListViewModel.fetchProjectList.observe(this, Observer { renderProjectList(it) })
    }

    private fun renderProjectList(projectList : ProjectListViewState) = when(projectList) {
        is ProjectListViewState.DataState -> updateProjectList(projectList.projects)
        is ProjectListViewState.ErrorState -> displayErrorDialog(projectList.error)
    }

    private fun updateProjectList(projectList: List<ProjectItem>) {
        (projectListView as? ProjectListView)?.updateList(projectList)
    }

    private fun displayErrorDialog(appException : AppException) {
        createInfoDialog(
            context = this,
            titleId = appException.titleResId,
            message = appException.getErrorMessage(this)
        ).show()
    }

    override fun onProjectItemInteraction(item: ProjectItem) {
    }
}
