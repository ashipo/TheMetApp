package com.ashipo.metropolitanmuseum.ui.searchresult

import com.ashipo.metropolitanmuseum.MainDispatcherExtension
import com.ashipo.metropolitanmuseum.data.TestSearchResultRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
@DisplayName("SearchResultScreenModel")
class SearchResultScreenModelTest {

    @Test
    fun `uiState - initial value - is Loading`() = runTest {
        val searchResultRepository = TestSearchResultRepository()
        val subject = SearchResultScreenModel("Query", searchResultRepository)

        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Loading, actual)
    }

    @Test
    @DisplayName("uiState - when search returns Result.failure - is Error")
    fun `uiState - when search returns Result_failure - is Error`() = runTest {
        val searchResultRepository = TestSearchResultRepository()
        searchResultRepository.setSearchResult(
            Result.failure(IOException("test exception"))
        )
        val subject = SearchResultScreenModel("Query", searchResultRepository)

        advanceUntilIdle()
        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Error, actual)
    }

    @Test
    @DisplayName("uiState - when search returns Result.success - is Success")
    fun `uiState - when search returns Result_success - is Success`() = runTest {
        val resultIds = listOf(1, 2, 777)
        val searchResultRepository = TestSearchResultRepository()
        searchResultRepository.setSearchResult(
            Result.success(resultIds)
        )
        val subject = SearchResultScreenModel("Query", searchResultRepository)

        advanceUntilIdle()
        val actual = subject.uiState.value

        assertEquals(SearchResultUiState.Success(resultIds.size), actual)
    }
}
