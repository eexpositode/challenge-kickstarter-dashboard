package com.eexposito.kickstarterdashboard.viewmodels

import com.eexposito.kickstarterdashboard.api.Project
import com.eexposito.kickstarterdashboard.helpers.AppException

interface ViewState

sealed class ProjectListViewState :
    ViewState {
    data class DataState(val projects: List<ProjectItem>) : ProjectListViewState()
    data class ErrorState(val error: AppException) : ProjectListViewState()
}

sealed class ProjectViewState :
    ViewState {
    data class DataState(val project: Project) : ProjectViewState()
    data class ErrorState(val error: AppException) : ProjectViewState()
}
