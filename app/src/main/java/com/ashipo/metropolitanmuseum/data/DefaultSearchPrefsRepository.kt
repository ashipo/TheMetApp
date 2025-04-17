package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.datastore.SearchPrefsDataSource

class DefaultSearchPrefsRepository(
    private val dataSource: SearchPrefsDataSource,
) : SearchPrefsRepository {

    override val searchPrefs = dataSource.searchPrefs

    override suspend fun setByTitle(value: Boolean) {
        dataSource.setByTitle(value)
    }

    override suspend fun setByArtistOrCulture(value: Boolean) {
        dataSource.setByArtistOrCulture(value)
    }

    override suspend fun setByTags(value: Boolean) {
        dataSource.setByTags(value)
    }

    override suspend fun setWithImage(value: Boolean) {
        dataSource.setWithImage(value)
    }

    override suspend fun setIsHighlight(value: Boolean) {
        dataSource.setIsHighlight(value)
    }

    override suspend fun setIsOnView(value: Boolean) {
        dataSource.setIsOnView(value)
    }

    override suspend fun setByDate(value: Boolean) {
        dataSource.setByDate(value)
    }

    override suspend fun setFromYear(year: Int) {
        dataSource.setFromYear(year)
    }

    override suspend fun setToYear(year: Int) {
        dataSource.setToYear(year)
    }

    override suspend fun setDepartmentId(id: Int) {
        dataSource.setDepartmentId(id)
    }

    override suspend fun setMedium(medium: List<String>) {
        dataSource.setMedium(medium)
    }

    override suspend fun setGeoLocation(geoLocation: List<String>) {
        dataSource.setGeoLocation(geoLocation)
    }
}
