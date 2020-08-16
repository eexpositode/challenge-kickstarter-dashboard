package com.eexposito.kickstarterdashboard.viewmodels

import com.eexposito.kickstarterdashboard.helpers.AppException

interface ViewState

sealed class ProjectsViewState : ViewState {
    object LoadingState : ProjectsViewState()
    data class DataState(val projects: List<ProjectItem>) : ProjectsViewState()
    data class ErrorState(val error: AppException) : ProjectsViewState()
}
