package com.ashipo.metropolitanmuseum.data.datastore

import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.copy
import com.ashipo.metropolitanmuseum.data.SearchPrefsRepository
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestSearchPrefsRepository : SearchPrefsRepository {

    private val _searchPrefs =
        MutableSharedFlow<SearchPrefs>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        _searchPrefs.tryEmit(searchPrefs {})
    }

    private val currentSearchPrefs = _searchPrefs.replayCache.firstOrNull() ?: searchPrefs {}

    override val searchPrefs: Flow<SearchPrefs> = _searchPrefs

    override suspend fun setByTitle(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { byTitle = value })
        }
    }

    override suspend fun setByArtistOrCulture(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { byArtistOrCulture = value })
        }
    }

    override suspend fun setByTags(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { byTags = value })
        }
    }

    override suspend fun setWithImage(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { withImage = value })
        }
    }

    override suspend fun setIsHighlight(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { isHighlight = value })
        }
    }

    override suspend fun setIsOnView(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { isOnView = value })
        }
    }

    override suspend fun setByDate(value: Boolean) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { byDate = value })
        }
    }

    override suspend fun setFromYear(year: Int) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { fromYear = year })
        }
    }

    override suspend fun setToYear(year: Int) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { toYear = year })
        }
    }

    override suspend fun setDepartmentId(id: Int) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy { departmentId = id })
        }
    }

    override suspend fun setMedium(newMedium: List<String>) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy {
                medium.clear()
                medium.addAll(newMedium)
            })
        }
    }

    override suspend fun setGeoLocation(newGeoLocation: List<String>) {
        currentSearchPrefs.let { current ->
            _searchPrefs.tryEmit(current.copy {
                geoLocation.clear()
                geoLocation.addAll(newGeoLocation)
            })
        }
    }

    /**
     * Test only
     */
    fun setSearchPrefs(newPrefs: SearchPrefs) {
        _searchPrefs.tryEmit(newPrefs)
    }
}
