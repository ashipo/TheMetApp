package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.SearchPrefs
import kotlinx.coroutines.flow.Flow

interface SearchPrefsRepository {

    val searchPrefs: Flow<SearchPrefs>

    suspend fun setByTitle(value: Boolean)
    suspend fun setByArtistOrCulture(value: Boolean)
    suspend fun setByTags(value: Boolean)
    suspend fun setWithImage(value: Boolean)
    suspend fun setIsHighlight(value: Boolean)
    suspend fun setIsOnView(value: Boolean)
    suspend fun setByDate(value: Boolean)
    suspend fun setFromYear(year: Int)
    suspend fun setToYear(year: Int)
    suspend fun setDepartmentId(id: Int)
    suspend fun setMedium(medium: List<String>)
    suspend fun setGeoLocation(geoLocation: List<String>)
}
