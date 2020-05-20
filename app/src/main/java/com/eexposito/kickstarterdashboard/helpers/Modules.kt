package com.eexposito.kickstarterdashboard.helpers

import com.eexposito.kickstarterdashboard.viewmodels.ProjectListViewModel
import com.eexposito.kickstarterdashboard.api.KickstarterApiManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.android.viewmodel.dsl.viewModel

val appModule = module {

    viewModel {
        ProjectListViewModel(
            get()
        )
    }
    single {
        KickstarterApiManager(
            androidApplication()
        )
    }
}
