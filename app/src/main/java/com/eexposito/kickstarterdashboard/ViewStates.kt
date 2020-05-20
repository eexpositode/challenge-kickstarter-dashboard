package com.eexposito.kickstarterdashboard

interface ViewState

sealed class ProjectListViewState : ViewState {
    data class DataState(val projects: List<Project>) : ProjectListViewState()
    data class ErrorState(val error: AppException) : ProjectListViewState()
}

sealed class ProjectViewState : ViewState {
    data class DataState(val project: Project) : ProjectViewState()
    data class ErrorState(val error: AppException) : ProjectViewState()
}
