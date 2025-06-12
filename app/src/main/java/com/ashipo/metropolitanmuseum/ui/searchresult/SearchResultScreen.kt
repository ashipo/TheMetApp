@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.searchresult

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.LocalSharedTransitionScope
import com.ashipo.metropolitanmuseum.ui.model.Artwork
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.ashipo.metropolitanmuseum.ui.model.ArtworkInfo
import com.ashipo.metropolitanmuseum.ui.model.Constituent
import com.ashipo.metropolitanmuseum.ui.theme.MetropolitanMuseumTheme
import com.ashipo.metropolitanmuseum.ui.util.SharedElementType
import com.ashipo.metropolitanmuseum.ui.util.SharedKey
import com.ashipo.metropolitanmuseum.ui.util.SharedScopes
import com.ashipo.metropolitanmuseum.ui.util.openUrl
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.request.ComposableImageRequest
import com.github.panpf.sketch.request.placeholder
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    uiState: SearchResultUiState,
    pagingArtworks: LazyPagingItems<ArtworkInfo>,
    onAction: (SearchResultScreenAction) -> Unit,
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
                            val titleString = when (uiState) {
                                SearchResultUiState.Error -> stringResource(R.string.error)
                                SearchResultUiState.Loading -> stringResource(R.string.loading)
                                is SearchResultUiState.Success -> stringResource(R.string.found_template)
                                    .format(uiState.total)
                            }
                            Text(titleString)
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = dropUnlessResumed {
                                    onAction(SearchResultScreenAction.GoBack)
                                },
                                modifier = Modifier.testTag("navigateBack"),
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    stringResource(R.string.back)
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
                                "SearchResultScreen TopBar EnterExit"
                            )
                    )
                }
            }
        },
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)

        var detailedItemId by rememberSaveable { mutableIntStateOf(-1) }
        val toggleDetailed: (Int) -> Unit = { toggledId ->
            detailedItemId = if (detailedItemId == toggledId) {
                -1
            } else {
                toggledId
            }
        }
        when {
            uiState is SearchResultUiState.Loading
                    || pagingArtworks.loadState.refresh is LoadState.Loading ->
                LoadingIndicator(contentModifier)

            // If failed to load artwork ids list
            uiState is SearchResultUiState.Error ->
                ErrorIndicator({ onAction(SearchResultScreenAction.Search) }, contentModifier)

            // If failed to load individual artworks
            pagingArtworks.loadState.refresh is LoadState.Error ->
                ErrorIndicator(pagingArtworks::retry, contentModifier)

            else -> LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
            ) {
                items(
                    count = pagingArtworks.itemCount,
                    key = pagingArtworks.itemKey { it.id }
                ) { index ->
                    pagingArtworks[index]?.let { artworkInfo ->
                        Column {
                            HorizontalDivider()
                            when (artworkInfo) {
                                is Artwork -> Artwork(
                                    artwork = artworkInfo,
                                    onToggleDetailed = { toggleDetailed(artworkInfo.id) },
                                    detailed = detailedItemId == artworkInfo.id,
                                    onShowArtwork = dropUnlessResumed {
                                        onAction(SearchResultScreenAction.ShowArtwork(artworkInfo))
                                    },
                                    onOpenWebpage = {
                                        onAction(SearchResultScreenAction.OpenWebpage(artworkInfo))
                                    },
                                )

                                is ArtworkInfo.NotFound -> NotFoundPlaceholder(
                                    id = artworkInfo.id,
                                    onToggleDetailed = { toggleDetailed(artworkInfo.id) },
                                    detailed = detailedItemId == artworkInfo.id,
                                )
                            }
                        }
                    }
                }
                appendStateFooter(
                    appendState = pagingArtworks.loadState.append,
                    onRetry = pagingArtworks::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}

@Composable
private fun Artwork(
    artwork: Artwork,
    onToggleDetailed: () -> Unit,
    detailed: Boolean,
    onShowArtwork: () -> Unit,
    onOpenWebpage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val secondaryFields = remember(artwork) { getSecondaryFields(artwork) }
    Column(
        modifier = modifier
            .testTag("artwork:${artwork.id}")
    ) {
        val sharedTransitionScope = LocalSharedTransitionScope.current
            ?: throw IllegalStateException("No SharedTransitionScope found")
        val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
            ?: throw IllegalStateException("No AnimatedVisibilityScope found")
        with(sharedTransitionScope) {
            // Clickable top part
            Row(
                horizontalArrangement = spacedBy(8.dp),
                modifier = Modifier
                    .clickable { onToggleDetailed() }
                    .sharedBounds(
                        rememberSharedContentState(
                            SharedKey(artwork.id, SharedElementType.Bounds)
                        ),
                        animatedVisibilityScope,
                    )
                    .heightIn(min = 72.dp)
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    verticalArrangement = spacedBy(8.dp),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = AnnotatedString.fromHtml(artwork.title),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(
                                    SharedKey(artwork.id, SharedElementType.Title)
                                ),
                                animatedVisibilityScope,
                            )
                    )
                    secondaryFields.forEach { field ->
                        Text(
                            text = field,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                val previewSize = 104.dp
                if (artwork.images.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clickable(
                                role = Role.Button,
                                onClickLabel = stringResource(R.string.show_details),
                            ) { onShowArtwork() }
                            .padding(vertical = 8.dp)
                            .width(previewSize)
                            .fillMaxHeight()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_no_image),
                            contentDescription = stringResource(R.string.no_image),
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                } else {
                    with(sharedTransitionScope) {
                        AsyncImage(
                            request = ComposableImageRequest(artwork.images.first().previewUrl) {
                                placeholder(MaterialTheme.colorScheme.surfaceVariant)
                            },
                            contentDescription = stringResource(R.string.artwork_preview),
                            modifier = Modifier
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = stringResource(R.string.show_details),
                                ) { onShowArtwork() }
                                .padding(vertical = 8.dp)
                                .sharedBounds(
                                    rememberSharedContentState(
                                        SharedKey(artwork.id, SharedElementType.Image)
                                    ),
                                    animatedVisibilityScope,
                                    resizeMode = RemeasureToBounds,
                                )
                                .width(previewSize)
                                .heightIn(min = previewSize)
                                .fillMaxHeight()
                        )
                    }
                }
            }
            // Additional info and action buttons
            AnimatedVisibility(detailed) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    if (!artwork.isPublicDomain) {
                        Text(
                            text = stringResource(R.string.not_in_public_domain),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onOpenWebpage,
                            modifier = Modifier.testTag("openWebpage"),
                        ) {
                            Text(stringResource(R.string.artwork_page))
                        }
                        TextButton(
                            onClick = onShowArtwork,
                            modifier = Modifier.testTag("showArtwork"),
                        ) {
                            Text(stringResource(R.string.see_more))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Returns a list of the following fields, if present:
 * - name of the main artist OR culture
 * - date
 */
private fun getSecondaryFields(artwork: Artwork): List<AnnotatedString> = buildList {
    if (artwork.artistName.isNullOrBlank()) {
        artwork.culture?.let { add(AnnotatedString.fromHtml(it)) }
    } else {
        add(AnnotatedString.fromHtml(artwork.artistName))
    }

    artwork.date?.let { add(AnnotatedString.fromHtml(it)) }
}

private val notFoundSize = Modifier
    .heightIn(min = 56.dp)
    .fillMaxWidth()
    .padding(horizontal = 16.dp, vertical = 8.dp)

@Composable
private fun NotFoundPlaceholder(
    id: Int,
    onToggleDetailed: () -> Unit,
    detailed: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .clickable { onToggleDetailed() }
                .then(notFoundSize)
        ) {
            Text(
                text = stringResource(R.string.not_found_title_template).format(id),
                fontStyle = FontStyle.Italic,
            )
        }
        AnimatedVisibility(detailed) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(stringResource(R.string.not_found_desc))
                val context = LocalContext.current
                val artworkAddress = stringResource(R.string.artwork_url_template).format(id)
                TextButton(
                    onClick = {
                        openUrl(artworkAddress, context)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.artwork_page))
                }
            }
        }
    }
}

private fun LazyListScope.appendStateFooter(
    appendState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (appendState) {
        is LoadState.Loading -> item {
            HorizontalDivider()
            CircularProgressIndicator(
                modifier = modifier.wrapContentSize(Alignment.Center)
            )
        }

        is LoadState.Error -> item {
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.clickable(
                    role = Role.Button,
                    onClick = onRetry,
                )
            ) {
                Text(stringResource(R.string.error_loading_artworks))
            }
        }

        else -> Unit
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorIndicator(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(onRetry) {
            Icon(Icons.Default.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.try_again))
        }
    }
}

@Preview(
    name = "Night mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(
    name = "Day mode",
    showBackground = true,
)
@Composable
private fun ArtworkWithoutPreviewPreview() {
    val artwork = createArtwork(title = "Short title")
    var detailedView by remember { mutableStateOf(false) }
    MetropolitanMuseumTheme {
        Surface {
            SharedScopes {
                Artwork(artwork, { detailedView = !detailedView }, detailedView, {}, {})
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtworkErrorLoadingPreviewPreview() {
    val images = listOf(
        ArtworkImage(
            originalUrl = "original.com",
            largeUrl = "large.com",
            previewUrl = "preview.com",
        )
    )
    val artwork = createArtwork(images = images)
    var detailedView by remember { mutableStateOf(false) }
    SharedScopes {
        Artwork(artwork, { detailedView = !detailedView }, detailedView, {}, {})
    }
}

private fun createArtwork(
    id: Int = 880055,
    title: String = "Great Pyramid of Giza ".repeat(20),
    constituents: List<Constituent> = listOf(
        Constituent("Painter", "Picasso")
    ),
    isPublicDomain: Boolean = true,
    department: String = "Egyptian Art",
    culture: String = "B ".repeat(255),
    period: String = "Old Kingdom Old Kingdom Old 1 2 3 4 5 6 7 8 9 0 1 2 3 4",
    date: String = "2600 BC",
    geography: String = "Milky way, Solar system, Earth",
    medium: String = "Oil on canvas",
    classification: String = "Pyramid",
    objectPageUrl: String = "https://www.metmuseum.org/art/collection/search/753523",
    images: List<ArtworkImage> = emptyList(),
) = Artwork(
    id = id,
    title = title,
    constituents = constituents,
    period = period,
    date = date,
    geography = geography,
    culture = culture,
    medium = medium,
    classification = classification,
    department = department,
    objectPageUrl = objectPageUrl,
    isPublicDomain = isPublicDomain,
    images = images,
)

@Preview(showBackground = true)
@Composable
private fun NotFoundPlaceholderPreview() {
    var detailedView by remember { mutableStateOf(false) }
    NotFoundPlaceholder(12, { detailedView = !detailedView }, detailedView)
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    val items: List<ArtworkInfo> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SharedScopes {
        SearchResultScreen(
            uiState = SearchResultUiState.Error,
            pagingArtworks = pagingItems,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    val items: List<ArtworkInfo> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SharedScopes {
        SearchResultScreen(
            uiState = SearchResultUiState.Loading,
            pagingArtworks = pagingItems,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessScreenPreview() {
    val items: List<ArtworkInfo> = List(20) { i ->
        if (i % 4 == 0) {
            ArtworkInfo.NotFound(i)
        } else {
            createArtwork(id = i, title = "Artwork №$i", culture = "Culture", date = "Date")
        }
    }
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SharedScopes {
        SearchResultScreen(
            uiState = SearchResultUiState.Success(items.size),
            pagingArtworks = pagingItems,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppendStateFooterErrorPreview() {
    LazyColumn {
        item { Text("An item") }
        appendStateFooter(
            LoadState.Error(Throwable("Error message")),
            {},
            Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppendStateFooterLoadingPreview() {
    LazyColumn {
        item { Text("An item") }
        appendStateFooter(
            LoadState.Loading,
            {},
            Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}
