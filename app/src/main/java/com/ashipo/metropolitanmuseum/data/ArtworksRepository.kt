package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ArtworksRepository(
    private val networkDataSource: NetworkDataSource,
) {
    suspend fun getArtworks(ids: List<Int>): List<ArtworkResult> = coroutineScope {
        ids.map { id ->
            async {
                networkDataSource.getArtwork(id).also { result ->
                    if (result is Artwork) {
                        result.copy(primaryImagePreviewUrl = getImagePreviewUrl(result.primaryImageUrl))
                    }
                }
            }
        }.awaitAll()
    }
}
