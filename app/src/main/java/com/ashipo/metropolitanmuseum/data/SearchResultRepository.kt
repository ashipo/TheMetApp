package com.ashipo.metropolitanmuseum.data

import androidx.paging.PagingSource
import com.ashipo.metropolitanmuseum.ui.model.ArtworkInfo

interface SearchResultRepository {

    val pagingSourceFactory: () -> PagingSource<Int, ArtworkInfo>

    suspend fun search(searchQuery: String): Result<List<Int>>
}
