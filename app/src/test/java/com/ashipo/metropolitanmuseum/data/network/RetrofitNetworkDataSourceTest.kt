package com.ashipo.metropolitanmuseum.data.network

import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartment
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult
import com.ashipo.metropolitanmuseum.data.network.retrofit.RetrofitNetworkDataSource
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("RetrofitNetworkDataSource")
class RetrofitNetworkDataSourceTest {

    private lateinit var api: TestRetrofitMuseumApi
    private lateinit var subject: RetrofitNetworkDataSource

    @BeforeEach
    fun setup() {
        api = TestRetrofitMuseumApi()
        subject = RetrofitNetworkDataSource(api)
    }

    @Test
    fun `getDepartments - calls api correctly`() = runTest {
        val expectedDepartments = NetworkDepartments(
            listOf(
                NetworkDepartment(1, "Ancient Antarctica"),
                NetworkDepartment(3, "Modern Antarctica"),
            )
        )
        api.setDepartments(expectedDepartments)

        val actual = subject.getDepartments()

        assertEquals(expectedDepartments, actual)
    }

    @Test
    fun `search - calls api correctly`() = runTest {
        val expectedResult = SearchResult(3, listOf(1, 3, 37))
        api.setSearchResult(expectedResult)
        val expectedOptions = mapOf("query" to "portrait", "department" to "15")

        val actualResult = subject.search(expectedOptions)

        assertEquals(expectedResult, actualResult)
        assertEquals(expectedOptions, api.getSearchOptions())
    }

    @Test
    fun `getArtwork - returns existing artwork`() = runTest {
        val expected = NetworkArtwork(id = 123, isPublicDomain = true)
        api.addArtwork(expected)

        val actual = subject.getArtwork(123)

        assertEquals(expected, actual)
    }

    @Test
    fun `getArtwork - returns NotFound if api throws HttpException with code 404`() = runTest {
        val expected = ArtworkRequestResult.NotFound(123)

        val actual = subject.getArtwork(123)

        assertEquals(expected, actual)
    }
}
