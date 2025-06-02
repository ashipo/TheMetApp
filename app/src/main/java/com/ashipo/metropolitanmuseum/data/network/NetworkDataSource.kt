package com.ashipo.metropolitanmuseum.data.network

import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult

interface NetworkDataSource {

    suspend fun getDepartments(): NetworkDepartments

    suspend fun search(options: Map<String, String>): SearchResult

    suspend fun getArtwork(id: Int): ArtworkRequestResult
}
