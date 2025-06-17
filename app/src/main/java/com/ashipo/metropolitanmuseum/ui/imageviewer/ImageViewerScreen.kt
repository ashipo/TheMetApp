@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.imageviewer

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.dropUnlessResumed
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.LocalSharedTransitionScope
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.ashipo.metropolitanmuseum.ui.theme.MetropolitanMuseumTheme
import com.ashipo.metropolitanmuseum.ui.util.SharedElementType
import com.ashipo.metropolitanmuseum.ui.util.SharedKey
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.rememberSketchZoomState
import com.github.panpf.zoomimage.zoom.FixedScalesCalculator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageViewerScreen(
    images: List<ArtworkImage>,
    initialImageIndex: Int,
    artworkId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current
    val windowInsetsController = remember {
        val window = activity!!.window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    var showUI by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(showUI) {
        if (showUI) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
    // Show system UI on close
    DisposableEffect(windowInsetsController) {
        onDispose {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(modifier) {
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No Scope found")
        val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No Scope found")
        val pagerState = rememberPagerState(initialImageIndex) { images.size }
        // Close button
        AnimatedVisibility(
            visible = showUI,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopEnd)
        ) {
            with(sharedTransitionScope) {
                with(animatedVisibilityScope) {
                    CloseButton(
                        onClick = dropUnlessResumed { onNavigateBack() },
                        current = pagerState.currentPage + 1,
                        total = images.size,
                        modifier = Modifier
                            .windowInsetsPadding(
                                WindowInsets.systemBarsIgnoringVisibility
                                    .union(WindowInsets.displayCutout)
                            )
                            .padding(8.dp)
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f
                            )
                            .animateEnterExit()
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { index ->
            Box(
                contentAlignment = Alignment.Center,
            ) {
                val imageState = rememberAsyncImageState()
                val zoomState = rememberSketchZoomState()
                LaunchedEffect(zoomState.zoomable) {
                    zoomState.zoomable.scalesCalculator = FixedScalesCalculator(2f)
                    zoomState.zoomable.keepTransformWhenSameAspectRatioContentSizeChanged = true
                }
                val request = ComposableImageRequest(images[index].originalUrl) {
                    placeholder(ThumbnailMemoryCacheStateImage(images[index].largeUrl))
                    crossfade(fadeStart = false, durationMillis = 500)
                }

                with(sharedTransitionScope) {
                    SketchZoomAsyncImage(
                        request = request,
                        state = imageState,
                        zoomState = zoomState,
                        contentDescription = stringResource(R.string.artwork_image_template)
                            .format(index + 1),
                        onTap = { showUI = !showUI },
                        modifier = Modifier
                            .zIndex(1f)
                            .sharedBounds(
                                rememberSharedContentState(
                                    SharedKey(artworkId, SharedElementType.Image, index)
                                ),
                                animatedVisibilityScope,
                                resizeMode = ResizeMode.RemeasureToBounds,
                            )
                            .fillMaxSize()
                    )
                }
                var isPlaceholderCached by rememberSaveable { mutableStateOf(false) }
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    context.sketch.memoryCache.withLock(images[index].largeUrl) {
                        // TODO: Refactor this hack (ask Sketch dev for a better way?)
                        isPlaceholderCached = keys().any { it.startsWith(images[index].largeUrl) }
                    }
                }
                // Show UI when "Error, tap to reload"
                LaunchedEffect(imageState.loadState, isPlaceholderCached) {
                    if (!isPlaceholderCached && imageState.loadState is LoadState.Error) {
                        showUI = true
                    }
                }
                when (imageState.loadState) {
                    is LoadState.Started -> {
                        CircularProgressIndicator()
                    }

                    is LoadState.Error -> {
                        if (!isPlaceholderCached) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { imageState.restart() }
                                    .zIndex(2f)
                            ) {
                                Text(
                                    text = stringResource(R.string.reload_failed_image),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    // Success, Canceled, null
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val containerColor = ButtonDefaults.filledTonalButtonColors().containerColor.copy(alpha = 0.5f)
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        colors = ButtonDefaults.filledTonalButtonColors().copy(containerColor = containerColor),
        modifier = modifier
    ) {
        Text("$current / $total")
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.Close, stringResource(R.string.close))
    }
}

@PreviewLightDark
@Preview(name = "RTL", locale = "ar")
@Composable
private fun CloseButtonPreview() {
    MetropolitanMuseumTheme {
        CloseButton({}, 2, 11)
    }
}
