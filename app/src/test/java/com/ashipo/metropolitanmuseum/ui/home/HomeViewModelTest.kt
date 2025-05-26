package com.ashipo.metropolitanmuseum.ui.home

import com.ashipo.metropolitanmuseum.MainDispatcherExtension
import com.ashipo.metropolitanmuseum.data.datastore.TestDepartmentRepository
import com.ashipo.metropolitanmuseum.data.datastore.TestSearchPrefsRepository
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
@DisplayName("HomeViewModel")
class HomeViewModelTest {

    private lateinit var departmentRepository: TestDepartmentRepository
    private lateinit var searchPrefsRepository: TestSearchPrefsRepository
    private lateinit var subject: HomeViewModel

    @BeforeEach
    fun setup() {
        departmentRepository = TestDepartmentRepository()
        searchPrefsRepository = TestSearchPrefsRepository()
        subject = HomeViewModel(departmentRepository, searchPrefsRepository)
    }

    @Test
    fun `departments StateFlow - is backed by DepartmentRepository`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.departments.collect() }
        val expected = listOf(
            department { id = 1; name = "Dep 1" },
            department { id = 2; name = "Dep 2" },
        )

        departmentRepository.setDepartments(expected)
        advanceUntilIdle()
        val actual = subject.departments.value

        assertEquals(expected, actual)
    }

    @Test
    fun `searchPrefs StateFlow - is backed by SearchPrefsRepository`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.searchPrefs.collect() }
        val expected = searchPrefs {
            isHighlight = true
            isOnView = true
            geoLocation.addAll(listOf("Here", "There"))
        }

        searchPrefsRepository.setSearchPrefs(expected)
        advanceUntilIdle()
        val actual = subject.searchPrefs.value

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetByTitle) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetByTitle(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().byTitle

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetByArtistOrCulture) - updates SearchPrefsRepository`(expected: Boolean) =
        runTest {
            subject.onAction(HomeScreenAction.SetByArtistOrCulture(expected))
            advanceUntilIdle()
            val actual = searchPrefsRepository.searchPrefs.first().byArtistOrCulture

            assertEquals(expected, actual)
        }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetByTags) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetByTags(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().byTags

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetWithImage) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetWithImage(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().withImage

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetIsHighlight) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetIsHighlight(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().isHighlight

        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetIsOnView) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetIsOnView(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().isOnView

        assertEquals(expected, actual)
    }


    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `onAction(SetByDate) - updates SearchPrefsRepository`(expected: Boolean) = runTest {
        subject.onAction(HomeScreenAction.SetByDate(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().byDate

        assertEquals(expected, actual)
    }

    @Test
    fun `onAction(SetFromYear) - updates SearchPrefsRepository`() = runTest {
        val expected = 1663

        subject.onAction(HomeScreenAction.SetFromYear(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().fromYear

        assertEquals(expected, actual)
    }

    @Test
    fun `onAction(SetToYear) - updates SearchPrefsRepository`() = runTest {
        val expected = 1663

        subject.onAction(HomeScreenAction.SetToYear(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().toYear

        assertEquals(expected, actual)
    }

    @Test
    fun `onAction(SetDepartmentId) - updates SearchPrefsRepository`() = runTest {
        val expected = 156

        subject.onAction(HomeScreenAction.SetDepartmentId(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().departmentId

        assertEquals(expected, actual)
    }

    @Test
    fun `onAction(SetMedium) - updates SearchPrefsRepository`() = runTest {
        val expected = listOf("Walk", "like", "an", "Egyptian ")

        subject.onAction(HomeScreenAction.SetMedium(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().mediumList

        assertEquals(expected, actual)
    }

    @Test
    fun `onAction(SetGeoLocation) - updates SearchPrefsRepository`() = runTest {
        val expected = listOf("I", "was", "made", "for", "loving", "you")

        subject.onAction(HomeScreenAction.SetGeoLocation(expected))
        advanceUntilIdle()
        val actual = searchPrefsRepository.searchPrefs.first().geoLocationList

        assertEquals(expected, actual)
    }
}
