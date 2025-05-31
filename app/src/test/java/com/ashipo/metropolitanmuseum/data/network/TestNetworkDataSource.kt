package com.ashipo.metropolitanmuseum.data.network

import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult

class TestNetworkDataSource : NetworkDataSource {

    private var departments: NetworkDepartments = NetworkDepartments(emptyList())
    private var searchResult: SearchResult = SearchResult(0, emptyList())
    private val artworks = mutableMapOf<Int, ArtworkRequestResult>()

    private var searchOptions = mapOf<String, String>()

    override suspend fun getDepartments(): NetworkDepartments {
        return departments
    }

    override suspend fun search(options: Map<String, String>): SearchResult {
        searchOptions = options
        return searchResult
    }

    /**
     * Returns [ArtworkRequestResult] for the given [id] or throws if it was not added with
     * [addArtworkResult]
     *
     * @throws NoSuchElementException
     */
    override suspend fun getArtwork(id: Int): ArtworkRequestResult {
        return artworks.getValue(id)
    }

    // Test methods

    fun setDepartments(departments: NetworkDepartments) {
        this.departments = departments
    }

    fun setSearchResult(searchResult: SearchResult) {
        this.searchResult = searchResult
    }

    fun addArtworkResult(artworkRequestResult: ArtworkRequestResult) {
        artworks[artworkRequestResult.id] = artworkRequestResult
    }

    fun getSearchOptions(): Map<String, String> {
        return searchOptions
    }
}
