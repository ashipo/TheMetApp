package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ArtworksRepository(
    private val networkDataSource: NetworkDataSource,
) {
    suspend fun getArtworks(ids: List<Int>): List<ArtworkRequestResult> = coroutineScope {
        ids.map { id ->
            async {
                networkDataSource.getArtwork(id).let { result ->
                    if (result is NetworkArtwork) {
                        result.copy(primaryImagePreviewUrl = getPreviewImageUrl(result.primaryImageUrl))
                    } else {
                        result
                    }
                }
            }
        }.awaitAll()
    }
}
