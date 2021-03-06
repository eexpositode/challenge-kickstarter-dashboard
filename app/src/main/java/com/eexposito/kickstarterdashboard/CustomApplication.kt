package com.eexposito.kickstarterdashboard

import android.app.Application
import com.eexposito.kickstarterdashboard.helpers.appModule
import com.eexposito.kickstarterdashboard.helpers.persistenceModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import timber.log.Timber
import timber.log.Timber.DebugTree


class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CustomApplication)
            modules(listOf(appModule, persistenceModule))
        }
    }

    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }
}
