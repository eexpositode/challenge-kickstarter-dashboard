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

class ProjectsViewModel(private val projectRepository: ProjectRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val projectsPublisher: BehaviorProcessor<ProjectsViewState> by lazy {
        BehaviorProcessor.create<ProjectsViewState>()
    }

    val projects = projectsPublisher
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { ProjectsViewState.LoadingState }
        .toLiveData()

    fun fetchProjects() = projectRepository
        .getProjects()
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { projectsPublisher.onNext(ProjectsViewState.LoadingState) }
        .subscribe(
            {
                projectsPublisher.onNext(
                    ProjectsViewState.DataState(it.map { project -> ProjectItem(project) })
                )
            },
            {
                Timber.e(it)
                projectsPublisher.onNext(ProjectsViewState.ErrorState(it.toAppException()))
            }
        )
        .addToComposite()

    fun searchProjects(searchString: String?) = getValidProjects()
        .map<ProjectsViewState> { it.filterTitleBySearchString(searchString) }
        .doOnError { Timber.e(it) }
        .onErrorResumeNext { error: Throwable ->
            Single.just(ProjectsViewState.ErrorState(error.toAppException()))
        }
        .toFlowable()
        .toLiveData()

    private fun ProjectsViewState.DataState.filterTitleBySearchString(searchString: String?) =
        ProjectsViewState.DataState(
            if (searchString.isNullOrBlank())
                projects
            else
                projects.filter {
                    it.title.contains(
                        searchString.trimStart(' ').trimEnd(' '), ignoreCase = true
                    )
                }
        )

    private var isSortingAscending = true

    enum class SortMethod { BY_TITLE, BY_TIME_LEFT }

    fun sortProjects(sortMethod: SortMethod) = getValidProjects()
        .map<ProjectsViewState> {
            when (sortMethod) {
                SortMethod.BY_TITLE -> it.sortByTitle(isSortingAscending)
                SortMethod.BY_TIME_LEFT -> it.sortByTimeLeft(isSortingAscending)
            }
        }
        .doFinally { isSortingAscending = !isSortingAscending }
        .doOnError { Timber.e(it) }
        .onErrorResumeNext { error: Throwable ->
            Single.just(ProjectsViewState.ErrorState(error.toAppException()))
        }
        .toFlowable()
        .toLiveData()

    private fun ProjectsViewState.DataState.sortByTitle(isSortingAscending: Boolean) =
        ProjectsViewState.DataState(
            if (isSortingAscending) projects.sortedBy { it.title }
            else projects.sortedByDescending { it.title }
        )

    private fun ProjectsViewState.DataState.sortByTimeLeft(isSortingAscending: Boolean) =
        ProjectsViewState.DataState(
            if (isSortingAscending) projects.sortedBy { it.daysLeft }
            else projects.sortedByDescending { it.daysLeft }
        )

    fun filterProjectsByBackersRange(from: Int? = null, to: Int? = null) = Flowable
        .defer {
            when {
                from == null && to == null -> projectsPublisher
                from == null -> filterProjectsByBackersRange { it <= to!! }
                to == null -> filterProjectsByBackersRange { it >= from }
                else -> filterProjectsByBackersRange { it in from..to }
            }
        }
        .subscribeOn(Schedulers.io())
        .toLiveData()

    private fun filterProjectsByBackersRange(comparison: (Int) -> Boolean) = getValidProjects()
        .map<ProjectsViewState> { dataState ->
            ProjectsViewState.DataState(dataState.projects.filter { projectItem ->
                comparison(projectItem.backers)
            })
        }
        .doOnError { Timber.e(it) }
        .onErrorResumeNext { error: Throwable ->
            Single.just(ProjectsViewState.ErrorState(error.toAppException()))
        }
        .toFlowable()

    private fun getValidProjects() = projectsPublisher
        .ofType(ProjectsViewState.DataState::class.java)
        .firstOrError()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun Disposable.addToComposite() = also { compositeDisposable.add(it) }
}
