package com.ashipo.metropolitanmuseum.ui.searchresult

import com.ashipo.metropolitanmuseum.ui.model.Artwork

sealed interface SearchResultScreenAction {
    data object Search : SearchResultScreenAction
    data object GoBack : SearchResultScreenAction
    data class ShowArtwork(val artwork: Artwork) : SearchResultScreenAction
    data class OpenWebpage(val artwork: Artwork) : SearchResultScreenAction
}
