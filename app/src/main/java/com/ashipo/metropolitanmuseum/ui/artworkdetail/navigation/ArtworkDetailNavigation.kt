package com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailScreen
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailViewModel
import com.ashipo.metropolitanmuseum.ui.model.ImageViewerParams
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf

@Serializable
data class ArtworkDetailRoute(val artwork: Artwork)

fun NavController.navigateToArtworkDetail(artwork: Artwork) {
    navigate(ArtworkDetailRoute(artwork))
}

fun NavGraphBuilder.artworkDetailScreen(
    onShowFullscreen: (ImageViewerParams) -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable<ArtworkDetailRoute>(
        typeMap = mapOf(typeOf<Artwork>() to ArtworkType)
    ) {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            ArtworkDetailRoute(onShowFullscreen, onNavigateUp)
        }
    }
}

@Composable
fun ArtworkDetailRoute(
    onShowFullscreen: (ImageViewerParams) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtworkDetailViewModel = koinViewModel(),
) {
    ArtworkDetailScreen(
        uiState = viewModel.uiState,
        onNavigateBack = onNavigateUp,
        onShowFullscreen = { imageIndex ->
            onShowFullscreen(ImageViewerParams(viewModel.uiState.images, imageIndex))
        },
        modifier = modifier,
    )
}
