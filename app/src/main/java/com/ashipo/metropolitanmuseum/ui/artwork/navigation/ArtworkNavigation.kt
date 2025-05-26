package com.ashipo.metropolitanmuseum.ui.artwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkScreenContent
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkScreenModel
import com.ashipo.metropolitanmuseum.ui.model.ImageViewerParams
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf

@Serializable
data class ArtworkRoute(val artwork: Artwork)

fun NavController.navigateToArtwork(artwork: Artwork) {
    navigate(ArtworkRoute(artwork))
}

fun NavGraphBuilder.artworkScreen(
    onShowFullscreen: (ImageViewerParams) -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable<ArtworkRoute>(
        typeMap = mapOf(typeOf<Artwork>() to ArtworkType)
    ) {
        ArtworkRoute(onShowFullscreen, onNavigateUp)
    }
}

@Composable
fun ArtworkRoute(
    onShowFullscreen: (ImageViewerParams) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtworkScreenModel = koinViewModel(),
) {
    ArtworkScreenContent(
        uiState = viewModel.uiState,
        onNavigateBack = onNavigateUp,
        onShowFullscreen = { imageIndex ->
            onShowFullscreen(ImageViewerParams(viewModel.uiState.images, imageIndex))
        },
        modifier = modifier,
    )
}
