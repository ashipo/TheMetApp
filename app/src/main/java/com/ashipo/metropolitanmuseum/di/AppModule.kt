package com.ashipo.metropolitanmuseum.di

import com.ashipo.metropolitanmuseum.data.di.dataModule
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkScreenModel
import com.ashipo.metropolitanmuseum.ui.home.HomeScreenModel
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultScreenModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(dataModule)

    viewModelOf(::HomeScreenModel)
    viewModelOf(::SearchResultScreenModel)
    viewModelOf(::ArtworkScreenModel)
}
