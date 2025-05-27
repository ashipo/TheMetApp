package com.ashipo.metropolitanmuseum.ui.imageviewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.zoomimage.SketchZoomAsyncImage
import com.github.panpf.zoomimage.rememberSketchZoomState
import com.github.panpf.zoomimage.zoom.FixedScalesCalculator

@Composable
fun ImageViewerScreen(
    images: List<ArtworkImage>,
    initialImageIndex: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        FilledTonalIconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopEnd)
                .safeDrawingPadding()
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Close, stringResource(R.string.close))
        }

        val pagerState = rememberPagerState(initialImageIndex) { images.size }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { index ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
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

                var isPlaceholderCached by rememberSaveable { mutableStateOf(false) }
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val sketch = context.sketch
                    val memoryCache = sketch.memoryCache
                    memoryCache.withLock(images[index].largeUrl) {
                        // TODO: Refactor this hack (ask author for a better way?)
                        isPlaceholderCached = keys().any { it.startsWith(images[index].largeUrl) }
                    }
                }

                SketchZoomAsyncImage(
                    request = request,
                    state = imageState,
                    zoomState = zoomState,
                    contentDescription = stringResource(R.string.artwork_image_template)
                        .format(index + 1),
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                )

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
