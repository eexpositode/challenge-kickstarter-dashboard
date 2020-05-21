package com.eexposito.kickstarterdashboard.viewmodels

import com.eexposito.kickstarterdashboard.helpers.AppException

interface ViewState

sealed class ProjectListViewState : ViewState {
    object LoadingState : ProjectListViewState()
    data class DataState(val projects: List<ProjectItem>) : ProjectListViewState()
    data class ErrorState(val error: AppException) : ProjectListViewState()
}
