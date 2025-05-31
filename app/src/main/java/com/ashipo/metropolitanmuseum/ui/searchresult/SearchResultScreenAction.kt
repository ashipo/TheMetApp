package com.ashipo.metropolitanmuseum.ui.searchresult

import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork

sealed interface SearchResultScreenAction {
    data object Search : SearchResultScreenAction
    data object GoBack : SearchResultScreenAction
    data class ShowArtwork(val artwork: NetworkArtwork) : SearchResultScreenAction
    data class OpenWebpage(val artwork: NetworkArtwork) : SearchResultScreenAction
}
