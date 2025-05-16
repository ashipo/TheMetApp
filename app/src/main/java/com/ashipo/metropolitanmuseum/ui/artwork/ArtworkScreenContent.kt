package com.ashipo.metropolitanmuseum.ui.artwork

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.rememberAsyncImagePainter
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.util.buildDescriptionString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkScreenContent(
    uiState: ArtworkScreenState,
    onNavigateBack: () -> Unit,
    onShowFullscreen: (imagesUrls: List<String>, initialImageIndex: Int) -> Unit,
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
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
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
                    onShowFullscreen = { imageIndex ->
                        val images = uiState.images.map { it.imageUrl }
                        onShowFullscreen(images, imageIndex)
                    },
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
    onShowFullscreen: (imageIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        var currentImageIndex by rememberSaveable { mutableIntStateOf(0) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .height(300.dp)
                .clickable { onShowFullscreen(currentImageIndex) }
                .fillMaxWidth()
        ) {
            val painter = rememberAsyncImagePainter(images[currentImageIndex].imageUrl)
            val painterState by painter.state.collectAsState()
            when (painterState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.artwork_image_template)
                            .format(currentImageIndex + 1),
                        modifier = Modifier.testTag("image")
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .testTag("previews")
        ) {
            items(
                count = images.size,
                key = { images[it].previewUrl }
            ) { index ->
                AsyncImage(
                    model = images[index].previewUrl,
                    contentDescription = stringResource(R.string.artwork_image_preview_template)
                        .format(index + 1),
                    modifier = previewSize
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { currentImageIndex = index }
                        .testTag("preview:$index")
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=tall,navigation=buttons",
    showSystemUi = true,
)
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
        ArtworkScreenContent(uiState, {}, { _, _ -> })
    }
}
