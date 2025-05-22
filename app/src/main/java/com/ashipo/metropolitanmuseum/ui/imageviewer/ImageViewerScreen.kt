package com.ashipo.metropolitanmuseum.ui.imageviewer

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageViewerScreen(
    val images: List<ArtworkImage>,
    val initialImageIndex: Int,
) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ImageViewerScreenContent(
            images = images,
            initialImageIndex = initialImageIndex,
            onNavigateBack = navigator::pop,
        )
    }
}
