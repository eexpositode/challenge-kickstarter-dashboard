package com.eexposito.kickstarterdashboard.helpers

import com.eexposito.kickstarterdashboard.viewmodels.ProjectsViewModel
import com.eexposito.kickstarterdashboard.api.KickstarterApiManager
import com.eexposito.kickstarterdashboard.persistence.AppDatabase
import com.eexposito.kickstarterdashboard.repositories.ProjectRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.android.viewmodel.dsl.viewModel

val appModule = module {

    viewModel { ProjectsViewModel(get()) }
    single { KickstarterApiManager(androidApplication()) }
    single { ProjectRepository(get(), get())}
}
val persistenceModule = module {
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().projectDao() }
}
