package com.ashipo.metropolitanmuseum.ui.searchresult

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkScreen
import com.ashipo.metropolitanmuseum.ui.util.openUrl
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class SearchResultScreen(val searchQuery: String) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SearchResultScreenModel>(
            parameters = { parametersOf(searchQuery) }
        )
        val uiState = screenModel.uiState.collectAsStateWithLifecycle().value
        val pagingArtworks = screenModel.pagingArtworks.collectAsLazyPagingItems()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        SearchResultScreenContent(
            uiState = uiState,
            pagingArtworks = pagingArtworks,
            onAction = { action ->
                when (action) {
                    is SearchResultScreenAction.GoBack ->
                        navigator.pop()

                    is SearchResultScreenAction.ShowArtwork ->
                        navigator.push(ArtworkScreen(action.artwork))

                    is SearchResultScreenAction.OpenWebpage ->
                        openUrl(action.artwork.objectPageUrl, context)

                    else -> screenModel.onAction(action)
                }
            },
        )
    }
}
