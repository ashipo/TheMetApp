package com.ashipo.metropolitanmuseum.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ashipo.metropolitanmuseum.data.ArtworksRepository
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult

const val PAGE_SIZE = 15

class ArtworkPagingSource(
    private val artworksRepository: ArtworksRepository,
    private val artworkIds: List<Int>,
) : PagingSource<Int, ArtworkRequestResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArtworkRequestResult> {
        try {
            // Might be negative
            val pageKey = params.key ?: STARTING_KEY
            // Last index (exclusive)
            val toIndex = (pageKey + params.loadSize).coerceAtMost(artworkIds.size)
            // First index (inclusive)
            val fromIndex = pageKey.coerceAtLeast(STARTING_KEY)
            val pageArtworkIds = getArtworkIds(fromIndex, toIndex)
            val pageArtworks = artworksRepository.getArtworks(pageArtworkIds)
            val prevKey = when (fromIndex) {
                STARTING_KEY -> null
                // provide possibly negative params.key of the previous page
                else -> fromIndex - PAGE_SIZE
            }
            val nextKey = when (artworkIds.size) {
                toIndex -> null
                else -> toIndex
            }
            return LoadResult.Page(
                data = pageArtworks,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error for
            // expected errors (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArtworkRequestResult>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return (anchorPosition - state.config.initialLoadSize / 2).coerceAtLeast(STARTING_KEY)
    }

    /**
     * Get portion of the artworks Ids specified by [fromIndex] and [toIndex]. If [toIndex] is out
     * of bounds, returns the remaining part of the list.
     *
     * @param fromIndex inclusive
     * @param toIndex exclusive
     */
    private fun getArtworkIds(fromIndex: Int, toIndex: Int): List<Int> {
        if (artworkIds.isEmpty() || fromIndex > artworkIds.lastIndex) {
            return emptyList()
        }
        return artworkIds.subList(fromIndex, toIndex.coerceAtMost(artworkIds.size))
    }

    companion object {
        private const val STARTING_KEY = 0
    }
}
