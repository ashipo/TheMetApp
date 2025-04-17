package com.ashipo.metropolitanmuseum.data.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ashipo.metropolitanmuseum.Departments
import com.ashipo.metropolitanmuseum.LastUpdated
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.data.datastore.DepartmentsDataSource
import com.ashipo.metropolitanmuseum.data.datastore.DepartmentsSerializer
import com.ashipo.metropolitanmuseum.data.datastore.LastUpdatedDataSource
import com.ashipo.metropolitanmuseum.data.datastore.LastUpdatedSerializer
import com.ashipo.metropolitanmuseum.data.datastore.SearchPrefsDataSource
import com.ashipo.metropolitanmuseum.data.datastore.SearchPrefsSerializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

val datastoreModule = module {

    val departmentsName = "departments"
    val lastUpdatedName = "last_updated"
    val searchPrefsName = "search_prefs"

    single<DataStore<Departments>> {
        DataStoreFactory.create(
            serializer = DepartmentsSerializer,
        ) {
            androidContext().dataStoreFile("departments.pb")
        }
    } withOptions {
        named(departmentsName)
    }

    single<DataStore<LastUpdated>> {
        DataStoreFactory.create(
            serializer = LastUpdatedSerializer,
        ) {
            androidContext().dataStoreFile("last_updated.pb")
        }
    } withOptions {
        named(lastUpdatedName)
    }

    single<DataStore<SearchPrefs>> {
        DataStoreFactory.create(
            serializer = SearchPrefsSerializer,
        ) {
            androidContext().dataStoreFile("search_prefs.pb")
        }
    } withOptions {
        named(searchPrefsName)
    }

    single {
        SearchPrefsDataSource(get(named(searchPrefsName)))
    }

    single {
        DepartmentsDataSource(get(named(departmentsName)))
    }

    single {
        LastUpdatedDataSource(get(named(lastUpdatedName)))
    }
}
