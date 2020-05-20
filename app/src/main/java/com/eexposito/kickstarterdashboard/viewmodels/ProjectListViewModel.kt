package com.eexposito.kickstarterdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.eexposito.kickstarterdashboard.api.KickstarterApiManager
import com.eexposito.kickstarterdashboard.helpers.toAppException
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ProjectListViewModel(kickstarterApiManager: KickstarterApiManager) : ViewModel() {

    val fetchProjectList = kickstarterApiManager
        .fetchProjectList()
        .subscribeOn(Schedulers.io())
        .map<ProjectListViewState> {
            ProjectListViewState.DataState(it.map { project -> ProjectItem(project) })
        }
        .doOnError { Timber.e(it) }
        .onErrorReturn { error: Throwable ->
            ProjectListViewState.ErrorState(error.toAppException())
        }
        .toFlowable(BackpressureStrategy.BUFFER)
        .toLiveData()
}
