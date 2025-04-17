package com.ashipo.metropolitanmuseum.ui.artwork

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.rememberAsyncImagePainter
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.util.buildDescriptionString
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableContentLocation
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkScreenContent(
    uiState: ArtworkScreenState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.artwork))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("navigateBack"),
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.back))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.images.isNotEmpty()) {
                Images(
                    images = uiState.images,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(buildDescriptionString(stringResource(R.string.title), uiState.title))
                for (constituent in uiState.constituents) {
                    Text(buildDescriptionString(constituent.role, constituent.info))
                }
                if (uiState.period.isNotBlank()) {
                    Text(buildDescriptionString(stringResource(R.string.period), uiState.period))
                }
                if (uiState.date.isNotBlank()) {
                    Text(buildDescriptionString(stringResource(R.string.date), uiState.date))
                }
                if (uiState.geography.isNotBlank()) {
                    Text(
                        buildDescriptionString(
                            stringResource(R.string.geography),
                            uiState.geography
                        )
                    )
                }
                if (uiState.culture.isNotBlank()) {
                    Text(buildDescriptionString(stringResource(R.string.culture), uiState.culture))
                }
                if (uiState.medium.isNotBlank()) {
                    Text(buildDescriptionString(stringResource(R.string.medium), uiState.medium))
                }
                if (uiState.classification.isNotBlank()) {
                    Text(
                        buildDescriptionString(
                            stringResource(R.string.classification),
                            uiState.classification
                        )
                    )
                }
                if (uiState.department.isNotBlank()) {
                    Text(
                        buildDescriptionString(
                            stringResource(R.string.department),
                            uiState.department
                        )
                    )
                }
            }
        }
    }
}

private val previewSize = Modifier
    .requiredSize(100.dp)

/**
 * @param images must not be empty
 */
@Composable
private fun Images(
    images: List<ArtworkImage>,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        var showFullScreen by rememberSaveable { mutableStateOf(false) }
        var currentImageUrl by rememberSaveable { mutableStateOf(images.first().imageUrl) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .height(300.dp)
                .clickable { showFullScreen = !showFullScreen }
                .fillMaxWidth()
        ) {
            val painter = rememberAsyncImagePainter(currentImageUrl)
            val painterState by painter.state.collectAsState()
            when (painterState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.artwork_image),
                        modifier = Modifier.testTag("image:$currentImageUrl")
                    )
                    if (showFullScreen) {
                        Dialog(
                            onDismissRequest = { showFullScreen = false },
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                        ) {
                            val zoomableState = rememberZoomableState(ZoomSpec(4f)).apply {
                                setContentLocation(
                                    ZoomableContentLocation.scaledInsideAndCenterAligned(painter.intrinsicSize)
                                )
                            }
                            Box {
                                Image(
                                    painter = painter,
                                    contentDescription = stringResource(R.string.artwork_image),
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .fillMaxSize()
                                        .zoomable(zoomableState)
                                        .testTag("imageFullScreen:$currentImageUrl")
                                )
                                FilledTonalIconButton(
                                    onClick = { showFullScreen = false },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Close, stringResource(R.string.close))
                                }
                            }
                        }
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { painter.restart() }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.reload_failed_image),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            images.forEach { (previewUrl, url) ->
                AsyncImage(
                    model = previewUrl,
                    contentDescription = null,
                    modifier = previewSize
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { currentImageUrl = url }
                        .testTag("preview:$previewUrl")
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Composable
private fun ArtworkScreenPreview() {
    val uiState = ArtworkScreenState(
        id = 12345,
        title = "Great Pyramid of Giza",
        constituents = listOf(
            ConstituentInfo("Designer", "Asterix"),
            ConstituentInfo("Builder", "Obelix")
        ),
        date = "2600 BC",
        geography = "Egypt",
        medium = "Stone",
        images = listOf(
            ArtworkImage("p1", "m1"),
            ArtworkImage("p2", "m2"),
            ArtworkImage("p3", "m3"),
        ),
    )
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(color = Color.Cyan.toArgb(), width = 500, height = 500)
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        ArtworkScreenContent(uiState, {})
    }
}
