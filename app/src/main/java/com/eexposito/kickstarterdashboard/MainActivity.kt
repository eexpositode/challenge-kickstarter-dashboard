package com.eexposito.kickstarterdashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val projectListViewModel: ProjectListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)
        projectListViewModel.fetchProjectList.observe(this, Observer { renderProjectList(it) })
    }

    private fun renderProjectList(projectList : ProjectListViewState) = when(projectList) {
        is ProjectListViewState.DataState -> updateProjectList(projectList.projects)
        is ProjectListViewState.ErrorState -> displayErrorDialog(projectList.error)
    }

    private fun updateProjectList(projectList: List<Project>) {
        Timber.d("PROJECT LIST SIZE %d", projectList.size)
    }

    private fun displayErrorDialog(appException : AppException) {
        createInfoDialog(
            context = this,
            titleId = appException.titleResId,
            message = appException.getErrorMessage(this)
        ).show()
    }
}
