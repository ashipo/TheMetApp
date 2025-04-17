package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.copy
import kotlinx.coroutines.flow.Flow

class SearchPrefsDataSource(
    private val dataStore: DataStore<SearchPrefs>,
) {

    val searchPrefs: Flow<SearchPrefs> = dataStore.data

    suspend fun setByTitle(value: Boolean) {
        dataStore.updateData {
            it.copy { byTitle = value }
        }
    }

    suspend fun setByArtistOrCulture(value: Boolean) {
        dataStore.updateData {
            it.copy { byArtistOrCulture = value }
        }
    }

    suspend fun setByTags(value: Boolean) {
        dataStore.updateData {
            it.copy { byTags = value }
        }
    }

    suspend fun setWithImage(value: Boolean) {
        dataStore.updateData {
            it.copy { withImage = value }
        }
    }

    suspend fun setIsHighlight(value: Boolean) {
        dataStore.updateData {
            it.copy { isHighlight = value }
        }
    }

    suspend fun setIsOnView(value: Boolean) {
        dataStore.updateData {
            it.copy { isOnView = value }
        }
    }

    suspend fun setByDate(value: Boolean) {
        dataStore.updateData {
            it.copy { byDate = value }
        }
    }

    suspend fun setFromYear(year: Int) {
        dataStore.updateData {
            it.copy { fromYear = year }
        }
    }

    suspend fun setToYear(year: Int) {
        dataStore.updateData {
            it.copy { toYear = year }
        }
    }

    suspend fun setDepartmentId(id: Int) {
        dataStore.updateData {
            it.copy { departmentId = id }
        }
    }

    suspend fun setMedium(newMedium: List<String>) {
        dataStore.updateData {
            it.copy {
                medium.clear()
                medium.addAll(newMedium)
            }
        }
    }

    suspend fun setGeoLocation(newGeoLocation: List<String>) {
        dataStore.updateData {
            it.copy {
                geoLocation.clear()
                geoLocation.addAll(newGeoLocation)
            }
        }
    }
}
