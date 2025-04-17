package com.ashipo.metropolitanmuseum.data.di

import com.ashipo.metropolitanmuseum.BuildConfig
import com.ashipo.metropolitanmuseum.data.ArtworksRepository
import com.ashipo.metropolitanmuseum.data.DefaultDepartmentRepository
import com.ashipo.metropolitanmuseum.data.DefaultSearchPrefsRepository
import com.ashipo.metropolitanmuseum.data.DefaultSearchResultRepository
import com.ashipo.metropolitanmuseum.data.DepartmentRepository
import com.ashipo.metropolitanmuseum.data.SearchPrefsRepository
import com.ashipo.metropolitanmuseum.data.SearchResultRepository
import com.ashipo.metropolitanmuseum.data.datastore.di.datastoreModule
import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import com.ashipo.metropolitanmuseum.data.network.retrofit.RetrofitMuseumApi
import com.ashipo.metropolitanmuseum.data.network.retrofit.RetrofitNetworkDataSource
import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    includes(datastoreModule)

    singleOf(::createRetrofit)

    single<RetrofitMuseumApi> {
        val retrofit: Retrofit = get()
        retrofit.create(RetrofitMuseumApi::class.java)
    }

    singleOf(::RetrofitNetworkDataSource) bind NetworkDataSource::class

    singleOf(::DefaultDepartmentRepository) bind DepartmentRepository::class

    singleOf(::DefaultSearchPrefsRepository) bind SearchPrefsRepository::class

    singleOf(::ArtworksRepository)

    factoryOf(::DefaultSearchResultRepository) bind SearchResultRepository::class
}

/**
 * Creates Retrofit instance for museum API with rate limit of 80 requests per second
 */
private fun createRetrofit(): Retrofit {
    val limiter = object : Interceptor {
        //val rateLimiter = RateLimiter.create(80.0, Duration.ofSeconds(1))
        val rateLimiter = RateLimiter.create(80.0)
        override fun intercept(chain: Interceptor.Chain): Response {
            rateLimiter.acquire(1)
            return chain.proceed(chain.request())
        }
    }

//    val dispatcher = Dispatcher().apply { maxRequests = 5 }
    val clientBuilder = OkHttpClient.Builder()
        .addInterceptor(limiter)
    if (BuildConfig.DEBUG) {
        clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    }
    return Retrofit.Builder()
        .baseUrl("https://collectionapi.metmuseum.org/public/collection/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder.build())
        .build()
}
