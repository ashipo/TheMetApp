package com.ashipo.metropolitanmuseum.data.network.retrofit

import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RetrofitMuseumApi {

    @GET("departments")
    suspend fun getDepartments(): NetworkDepartments

    @GET("search")
    suspend fun search(@QueryMap options: Map<String, String>): SearchResult

    @GET("objects/{id}")
    suspend fun getArtwork(@Path("id") id: Int): Artwork
}
