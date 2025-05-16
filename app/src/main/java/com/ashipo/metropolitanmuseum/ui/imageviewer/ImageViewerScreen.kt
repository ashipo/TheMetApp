package com.ashipo.metropolitanmuseum.ui.imageviewer

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageViewerScreen(
    val imagesUrls: List<String>,
    val initialImageIndex: Int,
) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ImageViewerScreenContent(
            imagesUrls = imagesUrls,
            initialImageIndex = initialImageIndex,
            onNavigateBack = navigator::pop,
        )
    }
}
