package com.eexposito.kickstarterdashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ProjectListViewModel(kickstarterApiManager: KickstarterApiManager) : ViewModel() {

    val fetchProjectList = kickstarterApiManager
        .fetchProjectList()
        .subscribeOn(Schedulers.io())
        .map<ProjectListViewState> { ProjectListViewState.DataState(it) }
        .doOnError { Timber.e(it) }
        .onErrorReturn { error : Throwable ->
            ProjectListViewState.ErrorState(error.toAppException())
        }
        .toFlowable(BackpressureStrategy.BUFFER)
        .toLiveData()
}
