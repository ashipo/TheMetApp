package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.ui.model.ArtworkInfo
import com.ashipo.metropolitanmuseum.ui.model.toLocal
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ArtworksRepository(
    private val networkDataSource: NetworkDataSource,
) {
    suspend fun getArtworks(ids: List<Int>): List<ArtworkInfo> = coroutineScope {
        ids.map { id ->
            async {
                networkDataSource.getArtwork(id).let { result ->
                    when (result) {
                        is NetworkArtwork -> result.toLocal()
                        is ArtworkRequestResult.NotFound -> result.toLocal()
                    }
                }
            }
        }.awaitAll()
    }
}
