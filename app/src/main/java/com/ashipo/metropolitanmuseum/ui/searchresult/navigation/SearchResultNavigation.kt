package com.ashipo.metropolitanmuseum.ui.searchresult.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.ashipo.metropolitanmuseum.ui.model.Artwork
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultScreen
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultScreenAction
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultViewModel
import com.ashipo.metropolitanmuseum.ui.util.openUrl
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class SearchResultRoute(val query: String)

fun NavController.navigateToSearchResult(query: String) {
    navigate(SearchResultRoute(query))
}

fun NavGraphBuilder.searchResultScreen(
    onShowArtwork: (Artwork) -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable<SearchResultRoute> {
        SearchResultRoute(onShowArtwork, onNavigateUp)
    }
}

@Composable
fun SearchResultRoute(
    onShowArtwork: (Artwork) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val pagingArtworks = viewModel.pagingArtworks.collectAsLazyPagingItems()
    val context = LocalContext.current
    SearchResultScreen(
        uiState = uiState,
        pagingArtworks = pagingArtworks,
        onAction = { action ->
            when (action) {
                is SearchResultScreenAction.GoBack ->
                    onNavigateUp()

                is SearchResultScreenAction.ShowArtwork ->
                    onShowArtwork(action.artwork)

                is SearchResultScreenAction.OpenWebpage ->
                    action.artwork.objectPageUrl?.let { url ->
                        openUrl(url, context)
                    }

                else -> viewModel.onAction(action)
            }
        },
        modifier = modifier,
    )
}
