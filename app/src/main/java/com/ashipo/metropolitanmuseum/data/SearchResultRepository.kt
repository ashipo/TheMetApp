package com.ashipo.metropolitanmuseum.data

import androidx.paging.PagingSource
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult

interface SearchResultRepository {

    val pagingSourceFactory: () -> PagingSource<Int, ArtworkResult>

    suspend fun search(searchQuery: String): Result<List<Int>>
}
