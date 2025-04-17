package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("SearchPrefsDataSource")
class SearchPrefsDataSourceTest {

    private lateinit var dataStore: DataStore<SearchPrefs>
    private lateinit var subject: SearchPrefsDataSource

    @BeforeEach
    fun setup() {
        dataStore = TestDataStore(searchPrefs {})
        subject = SearchPrefsDataSource(dataStore)
    }

    @Test
    fun `setByTitle - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().byTitle

        subject.setByTitle(expected)
        val actual = dataStore.data.first().byTitle

        assertEquals(expected, actual)
    }

    @Test
    fun `setByArtistOrCulture - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().byArtistOrCulture

        subject.setByArtistOrCulture(expected)
        val actual = dataStore.data.first().byArtistOrCulture

        assertEquals(expected, actual)
    }

    @Test
    fun `setByTags - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().byTags

        subject.setByTags(expected)
        val actual = dataStore.data.first().byTags

        assertEquals(expected, actual)
    }

    @Test
    fun `setWithImage - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().withImage

        subject.setWithImage(expected)
        val actual = dataStore.data.first().withImage

        assertEquals(expected, actual)
    }

    @Test
    fun `setIsHighlight - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().isHighlight

        subject.setIsHighlight(expected)
        val actual = dataStore.data.first().isHighlight

        assertEquals(expected, actual)
    }

    @Test
    fun `setIsOnView - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().isOnView

        subject.setIsOnView(expected)
        val actual = dataStore.data.first().isOnView

        assertEquals(expected, actual)
    }

    @Test
    fun `setByDate - updates DataStore`() = runTest {
        val expected = !SearchPrefs.getDefaultInstance().byDate

        subject.setByDate(expected)
        val actual = dataStore.data.first().byDate

        assertEquals(expected, actual)
    }

    @Test
    fun `setFromYear - updates DataStore`() = runTest {
        val expected = SearchPrefs.getDefaultInstance().fromYear + 1

        subject.setFromYear(expected)
        val actual = dataStore.data.first().fromYear

        assertEquals(expected, actual)
    }

    @Test
    fun `setToYear - updates DataStore`() = runTest {
        val expected = SearchPrefs.getDefaultInstance().toYear + 1

        subject.setToYear(expected)
        val actual = dataStore.data.first().toYear

        assertEquals(expected, actual)
    }

    @Test
    fun `setDepartmentId - updates DataStore`() = runTest {
        val expected = SearchPrefs.getDefaultInstance().departmentId + 1

        subject.setDepartmentId(expected)
        val actual = dataStore.data.first().departmentId

        assertEquals(expected, actual)
    }

    @Test
    fun `setMedium - updates DataStore`() = runTest {
        val expected = listOf("Clay", "Unobtanium")

        subject.setMedium(expected)
        val actual = dataStore.data.first().mediumList

        assertEquals(expected, actual)
    }

    @Test
    fun `setGeoLocation - updates DataStore`() = runTest {
        val expected = listOf("Africa", "Uganda")

        subject.setGeoLocation(expected)
        val actual = dataStore.data.first().geoLocationList

        assertEquals(expected, actual)
    }

    @Test
    fun `searchPrefs Flow - is backed by DataStore`() = runTest {
        val expected = searchPrefs {
            byTags = true
            departmentId = 13
            toYear = 1337
        }
        dataStore.updateData { expected }

        val actual = subject.searchPrefs.first()

        assertEquals(expected, actual)
    }
}
