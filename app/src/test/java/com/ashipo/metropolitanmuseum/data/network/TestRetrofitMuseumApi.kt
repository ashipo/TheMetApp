package com.ashipo.metropolitanmuseum.data.network

import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult
import com.ashipo.metropolitanmuseum.data.network.retrofit.RetrofitMuseumApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class TestRetrofitMuseumApi : RetrofitMuseumApi {

    private lateinit var departments: NetworkDepartments
    private lateinit var searchResult: SearchResult
    private val artworks = mutableMapOf<Int, Artwork>()

    private var searchOptions = mapOf<String, String>()

    override suspend fun getDepartments(): NetworkDepartments {
        return departments
    }

    override suspend fun search(options: Map<String, String>): SearchResult {
        searchOptions = options
        return searchResult
    }

    /**
     * Returns an [Artwork] that was added with [addArtwork] or throws `404` [HttpException] if
     * there are no [Artwork] with such [id]
     *
     * @param id of the [Artwork] to return
     */
    override suspend fun getArtwork(id: Int): Artwork {
        return artworks[id] ?: throw HttpException(
            Response.error<String>(
                404,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )
    }

    fun setDepartments(departments: NetworkDepartments) {
        this.departments = departments
    }

    fun setSearchResult(searchResult: SearchResult) {
        this.searchResult = searchResult
    }

    fun addArtwork(artwork: Artwork) {
        artworks[artwork.id] = artwork
    }

    fun getSearchOptions(): Map<String, String> {
        return searchOptions
    }
}
