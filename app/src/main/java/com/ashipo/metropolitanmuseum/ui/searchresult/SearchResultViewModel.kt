package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ashipo.metropolitanmuseum.data.SearchResultRepository
import com.ashipo.metropolitanmuseum.data.network.PAGE_SIZE
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.ui.searchresult.navigation.SearchResultRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val searchResultRepository: SearchResultRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query = savedStateHandle.toRoute<SearchResultRoute>().query
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

    val pagingArtworks: Flow<PagingData<ArtworkRequestResult>> = pager.flow.cachedIn(viewModelScope)

    init {
        search()
    }

    private fun search() {
        viewModelScope.launch {
            _uiState.value = SearchResultUiState.Loading
            val searchResult = searchResultRepository.search(query).getOrNull()
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
