package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.ashipo.metropolitanmuseum.MainDispatcherRule
import com.ashipo.metropolitanmuseum.data.TestSearchResultRepository
import com.ashipo.metropolitanmuseum.ui.searchresult.navigation.SearchResultRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SearchResultViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `uiState - initial value - is Loading`() = runTest {
        val searchResultRepository = TestSearchResultRepository()
        val subject = SearchResultViewModel(
            searchResultRepository,
            SavedStateHandle(SearchResultRoute("Query")),
        )

        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Loading, actual)
    }

    @Test
    fun `uiState - when search returns Result_failure - is Error`() = runTest {
        val searchResultRepository = TestSearchResultRepository()
        searchResultRepository.setSearchResult(
            Result.failure(IOException("test exception"))
        )
        val subject = SearchResultViewModel(
            searchResultRepository,
            SavedStateHandle(SearchResultRoute("Query")),
        )

        advanceUntilIdle()
        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Error, actual)
    }

    @Test
    fun `uiState - when search returns Result_success - is Success`() = runTest {
        val resultIds = listOf(1, 2, 777)
        val searchResultRepository = TestSearchResultRepository()
        searchResultRepository.setSearchResult(
            Result.success(resultIds)
        )
        val subject = SearchResultViewModel(
            searchResultRepository,
            SavedStateHandle(SearchResultRoute("Query")),
        )

        advanceUntilIdle()
        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Success(resultIds.size), actual)
    }
}
