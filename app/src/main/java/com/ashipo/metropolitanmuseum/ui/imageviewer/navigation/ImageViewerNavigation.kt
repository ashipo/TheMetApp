package com.ashipo.metropolitanmuseum.ui.imageviewer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.imageviewer.ImageViewerScreen
import com.ashipo.metropolitanmuseum.ui.model.ImageViewerParams
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class ImageViewerRoute(val params: ImageViewerParams)

fun NavController.navigateToImageViewer(params: ImageViewerParams) {
    navigate(ImageViewerRoute(params))
}

fun NavGraphBuilder.imageViewerScreen(
    onNavigateUp: () -> Unit,
) {
    composable<ImageViewerRoute>(
        typeMap = mapOf(typeOf<ImageViewerParams>() to ImageViewerParamsType)
    ) { backstackEntry ->
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable,
        ) {
            val route = backstackEntry.toRoute<ImageViewerRoute>()
            ImageViewerRoute(route.params, onNavigateUp)
        }
    }
}

@Composable
fun ImageViewerRoute(
    params: ImageViewerParams,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ImageViewerScreen(
        images = params.images,
        initialImageIndex = params.initialImageIndex,
        onNavigateBack = onNavigateUp,
        modifier = modifier,
    )
}
