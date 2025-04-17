package com.ashipo.metropolitanmuseum.data

import android.util.Log
import androidx.paging.PagingSource
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import com.ashipo.metropolitanmuseum.data.network.ArtworkPagingSource
import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class DefaultSearchResultRepository(
    private val artworksRepository: ArtworksRepository,
    private val networkDataSource: NetworkDataSource,
    private val searchPrefsRepository: SearchPrefsRepository,
) : SearchResultRepository {

    private var artworkIds: List<Int> = emptyList()

    // The last created instance of PagingSource, stored to invalidate
    private var lastPagingSource: PagingSource<Int, ArtworkResult>? = null

    override val pagingSourceFactory = {
        ArtworkPagingSource(artworksRepository, artworkIds.toList())
            .also { lastPagingSource = it }
    }

    override suspend fun search(searchQuery: String): Result<List<Int>> {
        val searchPrefs = searchPrefsRepository.searchPrefs.first()
        val result = try {
            val searchResult =
                networkDataSource.search(buildSearchOptions(searchQuery, searchPrefs))
            Result.success(searchResult.objectIDs ?: emptyList())
        } catch (httpException: HttpException) {
            Log.e(TAG, "Http Error performing search: ", httpException)
            Result.failure(httpException)
        } catch (exception: Exception) {
            Log.e(TAG, "Error performing search: ", exception)
            Result.failure(exception)
        }
        artworkIds = result.getOrElse { emptyList() }
        lastPagingSource?.invalidate()
        return result
    }

    //IMPORTANT: The API works only if the "q" param goes last
    private fun buildSearchOptions(
        searchQuery: String,
        searchPrefs: SearchPrefs,
    ): Map<String, String> {
        val options = LinkedHashMap<String, String>()
        if (searchPrefs.isHighlight) {
            options["isHighlight"] = "true"
        }
        if (searchPrefs.byTitle) {
            options["title"] = "true"
        }
        if (searchPrefs.byArtistOrCulture) {
            options["artistOrCulture"] = "true"
        }
        if (searchPrefs.byTags) {
            options["tags"] = "true"
        }
        if (searchPrefs.withImage) {
            options["hasImages"] = "true"
        }
        if (searchPrefs.departmentId > 0) {
            options["departmentId"] = searchPrefs.departmentId.toString()
        }
        if (searchPrefs.byDate) {
            options["dateBegin"] = searchPrefs.fromYear.toString()
            options["dateEnd"] = searchPrefs.toYear.toString()
        }
        if (searchPrefs.mediumList.isNotEmpty()) {
            options["medium"] = searchPrefs.mediumList.joinToString("|")
        }
        if (searchPrefs.geoLocationList.isNotEmpty()) {
            options["geoLocation"] = searchPrefs.geoLocationList.joinToString("|")
        }
        options["q"] = searchQuery
        return options
    }
}

private const val TAG = "DefaultSearchResultRepository"
