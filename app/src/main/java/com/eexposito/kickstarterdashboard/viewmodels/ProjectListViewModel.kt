package com.eexposito.kickstarterdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.eexposito.kickstarterdashboard.api.KickstarterApiManager
import com.eexposito.kickstarterdashboard.helpers.AppException
import com.eexposito.kickstarterdashboard.helpers.toAppException
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class ProjectListViewModel(private val kickstarterApiManager: KickstarterApiManager) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val projectListPublisher: BehaviorProcessor<ProjectListViewState> by lazy {
        BehaviorProcessor.create<ProjectListViewState>()
    }

    val projectList = projectListPublisher
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { ProjectListViewState.LoadingState }
        .toLiveData()

    fun fetchProjectList() = kickstarterApiManager
        .fetchProjectList()
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { projectListPublisher.onNext(ProjectListViewState.LoadingState) }
        .subscribe(
            {
                projectListPublisher.onNext(
                    ProjectListViewState.DataState(it.map { project -> ProjectItem(project) })
                )
            },
            {
                Timber.e(it)
                projectListPublisher.onNext(ProjectListViewState.ErrorState(it.toAppException()))
            }
        )
        .addToComposite()

    private var isSortingAscending = true
    enum class SortMethod {
        BY_TITLE, BY_TIME_LEFT
    }

    fun sortProjectList(sortMethod: SortMethod) = projectListPublisher
        .subscribeOn(Schedulers.io())
        .firstOrError()
        .ofType(ProjectListViewState.DataState::class.java)
        .doOnSuccess { Timber.v("SORTING") }
        .map {
            when(sortMethod) {
                SortMethod.BY_TITLE -> it.sortByTitle()
                SortMethod.BY_TIME_LEFT -> it.sortByTimeLeft()
            }
        }
        .doFinally { isSortingAscending = !isSortingAscending }
        .subscribe(
            { projectListPublisher.onNext(it) },
            {
                Timber.e(it)
                projectListPublisher.onNext(ProjectListViewState.ErrorState(it.toAppException()))
            }
        )
        .addToComposite()

    private fun ProjectListViewState.DataState.sortByTitle() = ProjectListViewState.DataState(
        if (isSortingAscending) projects.sortedBy { it.title }
        else projects.sortedByDescending { it.title }
    )

    private fun ProjectListViewState.DataState.sortByTimeLeft() = ProjectListViewState.DataState(
        if (isSortingAscending) projects.sortedBy { it.daysLeft }
        else projects.sortedByDescending { it.daysLeft }
    )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun Disposable.addToComposite() = also { compositeDisposable.add(it) }
}
