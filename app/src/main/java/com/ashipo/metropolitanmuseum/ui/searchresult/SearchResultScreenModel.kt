package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ashipo.metropolitanmuseum.data.SearchResultRepository
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import com.ashipo.metropolitanmuseum.data.network.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val searchQuery: String,
    private val searchResultRepository: SearchResultRepository,
) : ScreenModel {
    private val _uiState = MutableStateFlow<SearchResultUiState>(SearchResultUiState.Loading)
    val uiState: StateFlow<SearchResultUiState>
        get() = _uiState

    private val pager = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE,
        ),
        // Should always return a new PagingSource when invoked as PagingSource instances are not reusable
        pagingSourceFactory = searchResultRepository.pagingSourceFactory,
    )

    val pagingArtworks: Flow<PagingData<ArtworkResult>> = pager.flow.cachedIn(screenModelScope)

    init {
        search()
    }

    private fun search() {
        screenModelScope.launch {
            _uiState.value = SearchResultUiState.Loading
            val searchResult = searchResultRepository.search(searchQuery).getOrNull()
            if (searchResult == null) {
                _uiState.value = SearchResultUiState.Error
            } else {
                _uiState.value = SearchResultUiState.Success(searchResult.size)
            }
        }
    }

    fun onAction(action: SearchResultScreenAction) {
        when (action) {
            is SearchResultScreenAction.Search -> search()
            else -> Unit
        }
    }
}

// Represents the state of the search request.
// The found artworks are loaded separately and their state doesn't affect this state.
sealed interface SearchResultUiState {

    data object Loading : SearchResultUiState

    data class Success(val total: Int) : SearchResultUiState

    data object Error : SearchResultUiState
}
