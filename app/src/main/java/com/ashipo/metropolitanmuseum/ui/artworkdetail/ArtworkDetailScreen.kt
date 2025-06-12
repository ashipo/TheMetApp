@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.dropUnlessResumed
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.LocalSharedTransitionScope
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailScreenAction.GoBack
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailScreenAction.ShowImages
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.ashipo.metropolitanmuseum.ui.model.Constituent
import com.ashipo.metropolitanmuseum.ui.util.SharedElementType
import com.ashipo.metropolitanmuseum.ui.util.SharedKey
import com.ashipo.metropolitanmuseum.ui.util.SharedScopes
import com.ashipo.metropolitanmuseum.ui.util.buildDescriptionString
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkDetailScreen(
    uiState: ArtworkDetailScreenState,
    onAction: (ArtworkDetailScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            val sharedTransitionScope = LocalSharedTransitionScope.current
                ?: throw IllegalStateException("No SharedTransitionScope found")
            val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
                ?: throw IllegalStateException("No AnimatedVisibilityScope found")
            with(sharedTransitionScope) {
                with(animatedVisibilityScope) {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.artwork))
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = dropUnlessResumed { onAction(GoBack) },
                                modifier = Modifier.testTag("navigateBack")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    stringResource(R.string.back),
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f
                            )
                            .animateEnterExit(
                                slideInVertically() + fadeIn(),
                                slideOutVertically() + fadeOut(),
                                "ArtworkDetailScreen TopBar EnterExit"
                            )
                    )
                }
            }
        },
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No SharedTransitionScope found")
        val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No AnimatedVisibilityScope found")
        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .sharedBounds(
                        rememberSharedContentState(
                            SharedKey(uiState.id, SharedElementType.Bounds)
                        ),
                        animatedVisibilityScope,
                    )
                    .fillMaxSize()
            ) {
                // Images
                if (uiState.images.isNotEmpty()) {
                    Images(
                        id = uiState.id,
                        images = uiState.images,
                        onShowImages = { onAction(ShowImages(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = AnnotatedString.fromHtml(uiState.title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    SharedKey(uiState.id, SharedElementType.Title)
                                ),
                                animatedVisibilityScope,
                            )
                    )
                    for (constituent in uiState.constituents) {
                        Text(buildDescriptionString(constituent.role, constituent.name, true))
                    }
                    uiState.period?.let { period ->
                        Text(buildDescriptionString(stringResource(R.string.period), period))
                    }
                    uiState.date?.let { date ->
                        Text(buildDescriptionString(stringResource(R.string.date), date))
                    }
                    uiState.culture?.let { culture ->
                        Text(buildDescriptionString(stringResource(R.string.culture), culture))
                    }
                    uiState.medium?.let { medium ->
                        Text(buildDescriptionString(stringResource(R.string.medium), medium))
                    }
                    uiState.geography?.let { geography ->
                        Text(buildDescriptionString(stringResource(R.string.geography), geography))
                    }
                    uiState.classification?.let { classification ->
                        Text(
                            buildDescriptionString(
                                stringResource(R.string.classification),
                                classification,
                            ),
                        )
                    }
                    uiState.department?.let { department ->
                        Text(
                            buildDescriptionString(stringResource(R.string.department), department),
                        )
                    }
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
    id: Int,
    images: List<ArtworkImage>,
    onShowImages: (imageIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        var currentImageIndex by rememberSaveable { mutableIntStateOf(0) }
        // zOrder:
        // 0 - Progress indicator. Shown below placeholder if it is present
        // 1 - Image
        // 2 - Error
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .height(300.dp)
                .clickable(
                    onClick = dropUnlessResumed { onShowImages(currentImageIndex) },
                )
                .fillMaxWidth()
        ) {
            val imageState = rememberAsyncImageState()
            val request = ComposableImageRequest(images[currentImageIndex].largeUrl) {
                placeholder(ThumbnailMemoryCacheStateImage(images[currentImageIndex].previewUrl))
                crossfade(fadeStart = false)
            }
            val sharedTransitionScope = LocalSharedTransitionScope.current
                ?: throw IllegalStateException("No SharedTransitionScope found")
            val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
                ?: throw IllegalStateException("No AnimatedVisibilityScope found")

            with(sharedTransitionScope) {
                AsyncImage(
                    request = request,
                    state = imageState,
                    contentDescription = stringResource(R.string.artwork_image_template)
                        .format(currentImageIndex + 1),
                    modifier = Modifier
                        .testTag("image")
                        .zIndex(1f)
                        .sharedBounds(
                            rememberSharedContentState(
                                SharedKey(id, SharedElementType.Image, currentImageIndex)
                            ),
                            animatedVisibilityScope,
                            resizeMode = RemeasureToBounds,
                        )
                        .fillMaxSize()
                )
                when (imageState.loadState) {
                    is LoadState.Started -> {
                        CircularProgressIndicator()
                    }

                    is LoadState.Error -> {
                        with(animatedVisibilityScope) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clickable { imageState.restart() }
                                    .zIndex(2f)
                                    .renderInSharedTransitionScopeOverlay(
                                        zIndexInOverlay = 1f
                                    )
                                    .animateEnterExit(
                                        label = "ArtworkDetailScreen reload image EnterExit"
                                    )
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                    .fillMaxSize()
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .testTag("previews")
        ) {
            items(
                count = images.size,
                key = { images[it].previewUrl }
            ) { index ->
                val imageState = rememberAsyncImageState()
                AsyncImage(
                    uri = images[index].previewUrl,
                    state = imageState,
                    contentDescription = stringResource(R.string.artwork_image_preview_template)
                        .format(index + 1),
                    modifier = previewSize
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            // TODO: Doesn't work sometimes
                            if (imageState.loadState is LoadState.Error) {
                                imageState.restart()
                            }
                            currentImageIndex = index
                        }
                        .testTag("preview:$index")
                )
            }
        }
    }
}

@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=tall,navigation=buttons",
    showSystemUi = true,
)
@Composable
private fun ArtworkDetailScreenPreview() {
    val uiState = ArtworkDetailScreenState(
        id = 12345,
        title = "Great Pyramid of Giza",
        constituents = listOf(
            Constituent("Designer", "Asterix"),
            Constituent("Builder", "Obelix")
        ),
        date = "2600 BC",
        geography = "Egypt",
        medium = "Stone",
        images = listOf(
            ArtworkImage("o1", "l1", "p1"),
            ArtworkImage("o2", "l2", "p2"),
            ArtworkImage("o3", "l3", "p3"),
        ),
    )
    SharedScopes {
        ArtworkDetailScreen(uiState, {})
    }
}
