package com.ashipo.metropolitanmuseum

import android.app.Application
import com.ashipo.metropolitanmuseum.di.appModule
import com.ashipo.metropolitanmuseum.sync.Sync
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.http.HurlStack
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application(), SingletonSketch.Factory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        Sync.start()
    }

    override fun createSketch(context: PlatformContext) =
        Sketch.Builder(context).apply {
            addIgnoreFetcherProvider(HurlHttpUriFetcherProvider::class)
            addComponents {
                val httpStack = HurlStack.Builder().apply {
                    connectTimeoutMillis(5000)
                    readTimeoutMillis(5000)
                }.build()
                addFetcher(HurlHttpUriFetcher.Factory(httpStack))
            }
        }.build()
}
