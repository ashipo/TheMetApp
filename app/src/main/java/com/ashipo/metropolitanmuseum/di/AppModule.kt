package com.ashipo.metropolitanmuseum.di

import com.ashipo.metropolitanmuseum.data.di.dataModule
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkViewModel
import com.ashipo.metropolitanmuseum.ui.home.HomeViewModel
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(dataModule)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchResultViewModel)
    viewModelOf(::ArtworkViewModel)
}
