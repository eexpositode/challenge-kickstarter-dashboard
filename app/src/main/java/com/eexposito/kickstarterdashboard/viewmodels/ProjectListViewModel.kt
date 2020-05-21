package com.eexposito.kickstarterdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.eexposito.kickstarterdashboard.helpers.toAppException
import com.eexposito.kickstarterdashboard.repositories.ProjectRepository
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProjectListViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val projectListPublisher: BehaviorProcessor<ProjectListViewState> by lazy {
        BehaviorProcessor.create<ProjectListViewState>()
    }

    val projectList = projectListPublisher
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { ProjectListViewState.LoadingState }
        .toLiveData()

    fun fetchProjectList() = projectRepository
        .getProjects()
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
        .ofType(ProjectListViewState.DataState::class.java)
        .firstOrError()
        .map<ProjectListViewState> {
            when (sortMethod) {
                SortMethod.BY_TITLE -> it.sortByTitle()
                SortMethod.BY_TIME_LEFT -> it.sortByTimeLeft()
            }
        }
        .doFinally { isSortingAscending = !isSortingAscending }
        .doOnError { Timber.e(it) }
        .onErrorResumeNext { error: Throwable ->
            Single.just(ProjectListViewState.ErrorState(error.toAppException()))
        }
        .toFlowable()
        .toLiveData()

    private fun ProjectListViewState.DataState.sortByTitle() = ProjectListViewState.DataState(
        if (isSortingAscending) projects.sortedBy { it.title }
        else projects.sortedByDescending { it.title }
    )

    private fun ProjectListViewState.DataState.sortByTimeLeft() = ProjectListViewState.DataState(
        if (isSortingAscending) projects.sortedBy { it.daysLeft }
        else projects.sortedByDescending { it.daysLeft }
    )

    fun filterProjectListByBackersRange(from: Int? = null, to: Int? = null) = Flowable
        .defer {
            when {
                from == null && to == null -> projectListPublisher
                from == null -> filterProjectListByBackersRange { it <= to!! }
                to == null -> filterProjectListByBackersRange { it >= from }
                else -> filterProjectListByBackersRange { it in from..to }
            }
        }
        .subscribeOn(Schedulers.io())
        .toLiveData()

    private fun filterProjectListByBackersRange(comparison: (Int) -> Boolean) =
        projectListPublisher
            .ofType(ProjectListViewState.DataState::class.java)
            .firstOrError()
            .map<ProjectListViewState> { dataState ->
                ProjectListViewState.DataState(dataState.projects.filter { projectItem ->
                    comparison(projectItem.backers)
                })
            }
            .doOnError { Timber.e(it) }
            .onErrorResumeNext { error: Throwable ->
                Single.just(ProjectListViewState.ErrorState(error.toAppException()))
            }
            .toFlowable()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun Disposable.addToComposite() = also { compositeDisposable.add(it) }
}
