package com.eexposito.kickstarterdashboard.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.eexposito.kickstarterdashboard.R
import com.eexposito.kickstarterdashboard.helpers.AppException
import com.eexposito.kickstarterdashboard.helpers.CustomTabsDelegate
import com.eexposito.kickstarterdashboard.helpers.createInfoDialog
import com.eexposito.kickstarterdashboard.helpers.setColorResId
import com.eexposito.kickstarterdashboard.viewmodels.ProjectItem
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewModel
import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ProjectListView.OnProjectItemInteractionListener {

    private val projectListViewModel: ProjectListViewModel by viewModel()
    private lateinit var customTabsDelegate: CustomTabsDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)
        customTabsDelegate = CustomTabsDelegate(this).apply {
            configure(
                color = R.color.colorPrimary,
                backButtonDrawable = ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.ic_arrow_back_black_24dp
                )?.apply { setColorResId(this@MainActivity, R.color.colorOnPrimary) }
            )
        }
        (projectListView as? ProjectListView)?.bind(this)
        projectListViewModel.fetchProjectList.observe(this, Observer { renderProjectList(it) })
    }

    private fun renderProjectList(projectList: ProjectListViewState) = when (projectList) {
        is ProjectListViewState.DataState -> updateProjectList(projectList.projects)
        is ProjectListViewState.ErrorState -> displayErrorDialog(projectList.error)
    }

    private fun updateProjectList(projectList: List<ProjectItem>) {
        (projectListView as? ProjectListView)?.updateList(projectList)
    }

    private fun displayErrorDialog(appException: AppException) {
        createInfoDialog(
            context = this,
            titleId = appException.titleResId,
            message = appException.getErrorMessage(this)
        ).show()
    }

    override fun onProjectItemInteraction(item: ProjectItem) {
        customTabsDelegate.openUrl(item.url)
    }
}
