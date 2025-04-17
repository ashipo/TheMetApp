package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.datastore.SearchPrefsDataSource
import com.ashipo.metropolitanmuseum.data.datastore.TestDataStore
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

@DisplayName("DefaultSearchPrefsRepository")
class DefaultSearchPrefsRepositoryTest {

    private lateinit var searchPrefsDataSource: SearchPrefsDataSource
    private lateinit var subject: DefaultSearchPrefsRepository

    @BeforeEach
    fun setup() {
        searchPrefsDataSource = SearchPrefsDataSource(TestDataStore(searchPrefs {}))
        subject = DefaultSearchPrefsRepository(searchPrefsDataSource)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `byTitle - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setByTitle(expected)

        val actual = subject.searchPrefs.first().byTitle

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setByTitle - updates DataSource`(expected: Boolean) = runTest {
        subject.setByTitle(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().byTitle

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `byArtistOrCulture - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setByArtistOrCulture(expected)

        val actual = subject.searchPrefs.first().byArtistOrCulture

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setByArtistOrCulture - updates DataSource`(expected: Boolean) = runTest {
        subject.setByArtistOrCulture(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().byArtistOrCulture

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `byTags - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setByTags(expected)

        val actual = subject.searchPrefs.first().byTags

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setByTags - updates DataSource`(expected: Boolean) = runTest {
        subject.setByTags(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().byTags

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `withImage - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setWithImage(expected)

        val actual = subject.searchPrefs.first().withImage

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setWithImage - updates DataSource`(expected: Boolean) = runTest {
        subject.setWithImage(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().withImage

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `isHighlight - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setIsHighlight(expected)

        val actual = subject.searchPrefs.first().isHighlight

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setIsHighlight - updates DataSource`(expected: Boolean) = runTest {
        subject.setIsHighlight(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().isHighlight

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `isOnView - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setIsOnView(expected)

        val actual = subject.searchPrefs.first().isOnView

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setIsOnView - updates DataSource`(expected: Boolean) = runTest {
        subject.setIsOnView(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().isOnView

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `byDate - is backed by DataSource`(expected: Boolean) = runTest {
        searchPrefsDataSource.setByDate(expected)

        val actual = subject.searchPrefs.first().byDate

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `setByDate - updates DataSource`(expected: Boolean) = runTest {
        subject.setByDate(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().byDate

        assertEquals(expected, actual)
    }

    @Test
    fun `fromYear - is backed by DataSource`() = runTest {
        val expected = 1984
        searchPrefsDataSource.setFromYear(expected)

        val actual = subject.searchPrefs.first().fromYear

        assertEquals(expected, actual)
    }

    @Test
    fun `setFromYear - updates DataSource`() = runTest {
        val expected = 1984

        subject.setFromYear(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().fromYear

        assertEquals(expected, actual)
    }

    @Test
    fun `toYear - is backed by DataSource`() = runTest {
        val expected = 1984
        searchPrefsDataSource.setToYear(expected)

        val actual = subject.searchPrefs.first().toYear

        assertEquals(expected, actual)
    }

    @Test
    fun `setToYear - updates DataSource`() = runTest {
        val expected = 1984

        subject.setToYear(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().toYear

        assertEquals(expected, actual)
    }

    @Test
    fun `departmentId - is backed by DataSource`() = runTest {
        val expected = 159
        searchPrefsDataSource.setDepartmentId(expected)

        val actual = subject.searchPrefs.first().departmentId

        assertEquals(expected, actual)
    }

    @Test
    fun `setDepartmentId - updates DataSource`() = runTest {
        val expected = 157

        subject.setDepartmentId(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().departmentId

        assertEquals(expected, actual)
    }

    @Test
    fun `medium - is backed by DataSource`() = runTest {
        val expected = listOf("Rock", "Stone")
        searchPrefsDataSource.setMedium(expected)

        val actual = subject.searchPrefs.first().mediumList

        assertEquals(expected, actual)
    }

    @Test
    fun `setMedium - updates DataSource`() = runTest {
        val expected = listOf("Rock", "Stone")

        subject.setMedium(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().mediumList

        assertEquals(expected, actual)
    }

    @Test
    fun `geoLocation - is backed by DataSource`() = runTest {
        val expected = listOf("South America", "Cuzco")
        searchPrefsDataSource.setGeoLocation(expected)

        val actual = subject.searchPrefs.first().geoLocationList

        assertEquals(expected, actual)
    }

    @Test
    fun `setGeoLocation - updates DataSource`() = runTest {
        val expected = listOf("South America", "Cuzco")

        subject.setGeoLocation(expected)
        val actual = searchPrefsDataSource.searchPrefs.first().geoLocationList

        assertEquals(expected, actual)
    }
}
