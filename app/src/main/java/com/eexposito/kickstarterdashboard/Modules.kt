package com.eexposito.kickstarterdashboard

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.android.viewmodel.dsl.viewModel

val appModule = module {

    viewModel { ProjectListViewModel(get()) }
    single { KickstarterApiManager(androidApplication()) }
}
