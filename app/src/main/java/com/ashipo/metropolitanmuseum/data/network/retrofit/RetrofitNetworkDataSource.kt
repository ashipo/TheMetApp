package com.ashipo.metropolitanmuseum.data.network.retrofit

import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult
import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import retrofit2.HttpException

class RetrofitNetworkDataSource(
    private val museumApi: RetrofitMuseumApi,
) : NetworkDataSource {

    override suspend fun getDepartments(): NetworkDepartments =
        museumApi.getDepartments()

    override suspend fun search(options: Map<String, String>): SearchResult =
        museumApi.search(options)

    override suspend fun getArtwork(id: Int): ArtworkRequestResult {
        return try {
            museumApi.getArtwork(id)
        } catch (e: HttpException) {
            // 404 is a special case - the artwork most likely exists, but is not
            // available through the public API.
            // Rethrow/ignore all the other exceptions.
            if (e.code() == 404) {
                ArtworkRequestResult.NotFound(id)
            } else {
                throw e
            }
        }
    }
}
