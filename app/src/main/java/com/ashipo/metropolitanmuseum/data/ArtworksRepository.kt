package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ArtworksRepository(
    private val networkDataSource: NetworkDataSource,
) {
    suspend fun getArtworks(ids: List<Int>): List<ArtworkResult> = coroutineScope {
        ids.map { id ->
            async {
                networkDataSource.getArtwork(id).let { result ->
                    if (result is Artwork) {
                        result.copy(primaryImagePreviewUrl = getImagePreviewUrl(result.primaryImageUrl))
                    } else {
                        result
                    }
                }
            }
        }.awaitAll()
    }
}
