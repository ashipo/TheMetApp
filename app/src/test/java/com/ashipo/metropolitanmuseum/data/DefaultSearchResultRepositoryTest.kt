package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.datastore.TestSearchPrefsRepository
import com.ashipo.metropolitanmuseum.data.network.model.SearchResult
import com.ashipo.metropolitanmuseum.data.network.TestNetworkDataSource
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("DefaultSearchResultRepository")
class DefaultSearchResultRepositoryTest {

    private lateinit var networkDataSource: TestNetworkDataSource
    private lateinit var searchPrefsRepo: TestSearchPrefsRepository
    private lateinit var subject: DefaultSearchResultRepository

    @BeforeEach
    fun setup() {
        networkDataSource = TestNetworkDataSource()
        searchPrefsRepo = TestSearchPrefsRepository()
        val artworksRepo = ArtworksRepository(networkDataSource)
        subject = DefaultSearchResultRepository(artworksRepo, networkDataSource, searchPrefsRepo)
    }

    @Test
    fun `search - is backed up by NetworkDataSource`() = runTest {
        val expectedIds = listOf(1, 2, 200)
        networkDataSource.setSearchResult(SearchResult(expectedIds.size, expectedIds))

        val actualResult = subject.search("Test")
        val actualIds = actualResult.getOrNull()

        assertTrue { actualResult.isSuccess }
        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun `search - correctly builds search options`() = runTest {
        val query = "Madonna"
        searchPrefsRepo.setSearchPrefs(
            searchPrefs {
                isHighlight = true
                byTitle = true
                departmentId = 42
                byDate = true
                fromYear = 1234
                toYear = 2345
                medium.addAll(listOf("Wood", "Gold"))
            }
        )

        subject.search(query)

        val actualSearchOptions = networkDataSource.getSearchOptions()
        assertEquals("true", actualSearchOptions.getValue("isHighlight"))
        assertEquals("true", actualSearchOptions.getValue("title"))
        assertFalse(actualSearchOptions.contains("artistOrCulture"))
        assertFalse(actualSearchOptions.contains("tags"))
        assertFalse(actualSearchOptions.contains("hasImages"))
        assertEquals("42", actualSearchOptions.getValue("departmentId"))
        assertEquals("1234", actualSearchOptions.getValue("dateBegin"))
        assertEquals("2345", actualSearchOptions.getValue("dateEnd"))
        assertEquals("Wood|Gold", actualSearchOptions.getValue("medium"))
        assertFalse(actualSearchOptions.contains("geoLocation"))
        assertEquals(query, actualSearchOptions.getValue("q"))
    }
}
