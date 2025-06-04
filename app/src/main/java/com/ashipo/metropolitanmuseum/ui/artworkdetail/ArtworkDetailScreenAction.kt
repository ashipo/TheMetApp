package com.ashipo.metropolitanmuseum.ui.artworkdetail

sealed interface ArtworkDetailScreenAction {
    data object GoBack : ArtworkDetailScreenAction
    data class ShowImages(val initialImageIndex: Int) : ArtworkDetailScreenAction
}
