package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.ArtworkPagingSource
import com.ashipo.metropolitanmuseum.data.network.TestNetworkDataSource

class TestSearchResultRepository : SearchResultRepository {

    private var searchResult: Result<List<Int>> = Result.success(emptyList())

    override val pagingSourceFactory = {
        val networkDataSource = TestNetworkDataSource()
        val artworksRepo = ArtworksRepository(networkDataSource)
        ArtworkPagingSource(artworksRepo, listOf(1, 2, 3))
    }

    override suspend fun search(searchQuery: String): Result<List<Int>> {
        return searchResult
    }

    fun setSearchResult(value: Result<List<Int>>) {
        searchResult = value
    }
}
