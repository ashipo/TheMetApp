package com.ashipo.metropolitanmuseum.ui.imageviewer

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.ashipo.metropolitanmuseum.R
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableContentLocation
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun ImageViewerScreenContent(
    imagesUrls: List<String>,
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

        val pagerState = rememberPagerState(initialImageIndex) { imagesUrls.size }
        HorizontalPager(
            state = pagerState,
        ) { index ->
            val painter = rememberAsyncImagePainter(imagesUrls[index])
            val painterState by painter.state.collectAsState()
            when (painterState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    val zoomableState = rememberZoomableState(ZoomSpec(4f)).apply {
                        setContentLocation(
                            ZoomableContentLocation.scaledInsideAndCenterAligned(painter.intrinsicSize)
                        )
                    }
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.artwork_image_template)
                            .format(index + 1),
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(zoomableState)
                    )
                }

                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { painter.restart() }
                    ) {
                        Text(
                            text = stringResource(R.string.reload_failed_image),
                        )
                    }
                }
            }
        }
    }
}
