package com.ashipo.metropolitanmuseum.data

import androidx.paging.PagingSource
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult

interface SearchResultRepository {

    val pagingSourceFactory: () -> PagingSource<Int, ArtworkRequestResult>

    suspend fun search(searchQuery: String): Result<List<Int>>
}
