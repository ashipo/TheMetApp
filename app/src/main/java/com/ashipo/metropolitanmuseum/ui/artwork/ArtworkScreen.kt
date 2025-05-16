package com.ashipo.metropolitanmuseum.ui.artwork

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.ui.imageviewer.ImageViewerScreen
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class ArtworkScreen(val artwork: Artwork) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ArtworkScreenModel>(
            parameters = { parametersOf(artwork) }
        )
        val navigator = LocalNavigator.currentOrThrow
        ArtworkScreenContent(
            uiState = screenModel.uiState,
            onNavigateBack = navigator::pop,
            onShowFullscreen = { images, initial ->
                navigator.push(ImageViewerScreen(images, initial))
            }
        )
    }
}
