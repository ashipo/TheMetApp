package com.ashipo.metropolitanmuseum

import android.app.Application
import com.ashipo.metropolitanmuseum.di.appModule
import com.ashipo.metropolitanmuseum.sync.Sync
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        Sync.start()
    }
}
