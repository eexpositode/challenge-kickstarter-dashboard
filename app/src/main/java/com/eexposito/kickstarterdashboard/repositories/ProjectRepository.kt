package com.eexposito.kickstarterdashboard.repositories

import com.eexposito.kickstarterdashboard.api.KickstarterApiManager
import com.eexposito.kickstarterdashboard.api.ProjectApiModel
import com.eexposito.kickstarterdashboard.persistence.ProjectDao
import com.eexposito.kickstarterdashboard.persistence.ProjectEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ProjectRepository(
    private val projectDao: ProjectDao, private val kickstarterApiManager: KickstarterApiManager
) {

    fun getProjects() = projectDao
        .getProjects()
        .doOnSubscribe { fetchProjectsFromApi() }!!

    private fun fetchProjectsFromApi() = Observable
        .interval(0, 3, TimeUnit.MINUTES, Schedulers.io())
        .doOnNext { fetchAndStoreProjects() }
        .subscribeOn(Schedulers.io())
        .subscribe()

    private fun fetchAndStoreProjects() = kickstarterApiManager
        .fetchProjectList()
        .flatMapCompletable {
            Completable
                .fromCallable { projectDao.insertProjects(it.toProjectEntities()) }
                .subscribeOn(Schedulers.io())
        }
        .doOnError { Timber.e(it) }
        .onErrorComplete()
        .subscribeOn(Schedulers.io())
        .subscribe()

    private fun List<ProjectApiModel>.toProjectEntities() = this
        // TODO This error need to be catched when parsing the JSON
        .filter { it.numBackers.toIntOrNull() != null }
        .map { apiModel ->
            with(apiModel) {
                ProjectEntity(
                    sNo = sNo,
                    amtPledged = amtPledged,
                    blurb = blurb,
                    by = by,
                    country = country,
                    currency = currency,
                    endTime = endTime,
                    location = location,
                    percentageFunded = percentageFunded,
                    numBackers = numBackers.toInt(),
                    state = state,
                    title = title,
                    type = type,
                    url = url
                )
            }
        }
}
