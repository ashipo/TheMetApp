package com.ashipo.metropolitanmuseum.di

import com.ashipo.metropolitanmuseum.data.di.dataModule
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailViewModel
import com.ashipo.metropolitanmuseum.ui.home.HomeViewModel
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultViewModel
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.HurlHttpUriFetcher
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.http.HurlStack
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(dataModule)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchResultViewModel)
    viewModelOf(::ArtworkDetailViewModel)

    single<Sketch> {
        Sketch.Builder(get()).apply {
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
}
