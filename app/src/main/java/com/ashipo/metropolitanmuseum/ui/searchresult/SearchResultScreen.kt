@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.ashipo.metropolitanmuseum.ui.util.SharedElementType
import com.ashipo.metropolitanmuseum.ui.util.SharedKey
import com.ashipo.metropolitanmuseum.ui.util.buildDescriptionString
import com.ashipo.metropolitanmuseum.ui.util.openUrl
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.rememberAsyncImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.github.panpf.sketch.request.LoadState as SketchLoadState

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
                        when (artworkInfo) {
                            is Artwork -> Artwork(
                                artwork = artworkInfo,
                                onToggleDetailed = {
                                    detailedItemId = if (detailedItemId == artworkInfo.id) {
                                        -1
                                    } else {
                                        artworkInfo.id
                                    }
                                },
                                detailed = detailedItemId == artworkInfo.id,
                                onShowArtwork = dropUnlessResumed {
                                    onAction(SearchResultScreenAction.ShowArtwork(artworkInfo))
                                },
                                onOpenWebpage = {
                                    onAction(SearchResultScreenAction.OpenWebpage(artworkInfo))
                                },
                            )

                            is ArtworkInfo.NotFound -> NotFoundPlaceholder(
                                artworkInfo.id,
                                Modifier.fillMaxWidth()
                            )
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

private val artworkSize = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .heightIn(min = 56.dp)

private data class SharedField(val text: AnnotatedString, val key: SharedKey)

@Composable
private fun Artwork(
    artwork: Artwork,
    onToggleDetailed: () -> Unit,
    detailed: Boolean,
    onShowArtwork: () -> Unit,
    onOpenWebpage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maker = stringResource(R.string.maker)
    val various = stringResource(R.string.various)
    val periodLabel = stringResource(R.string.period)
    val dateLabel = stringResource(R.string.date)
    val cultureLabel = stringResource(R.string.culture)
    val mediumLabel = stringResource(R.string.medium)
    val secondaryFields = remember(artwork) {
        buildList {
            if (artwork.constituents.isNotEmpty()) {
                val text = if (artwork.constituents.size == 1) {
                    val maker = artwork.constituents.first()
                    buildDescriptionString(maker.role, maker.name, true)
                } else {
                    buildDescriptionString(maker, various)
                }
                val key = SharedKey(artwork.id, SharedElementType.Creator)
                add(SharedField(text, key))
            }
            artwork.period?.let {
                val text = buildDescriptionString(periodLabel, it)
                val key = SharedKey(artwork.id, SharedElementType.Period)
                add(SharedField(text, key))
            }
            artwork.date?.let {
                val text = buildDescriptionString(dateLabel, it)
                val key = SharedKey(artwork.id, SharedElementType.Date)
                add(SharedField(text, key))
            }
            artwork.culture?.let {
                val text = buildDescriptionString(cultureLabel, it)
                val key = SharedKey(artwork.id, SharedElementType.Culture)
                add(SharedField(text, key))
            }
            artwork.medium?.let {
                val text = buildDescriptionString(mediumLabel, it)
                val key = SharedKey(artwork.id, SharedElementType.Medium)
                add(SharedField(text, key))
            }
        }
    }
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedTransitionScope found")
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No AnimatedVisibilityScope found")
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .testTag("artwork:${artwork.id}")
                .sharedBounds(
                    rememberSharedContentState(
                        SharedKey(artwork.id, SharedElementType.Bounds)
                    ),
                    animatedVisibilityScope,
                )
        ) {
            HorizontalDivider()
            // Clickable top part with the title and a piece of additional info
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable { onToggleDetailed() }
                    .then(artworkSize)
            ) {
                Text(
                    text = AnnotatedString.fromHtml(artwork.title),
                    fontWeight = FontWeight.Bold,
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
                secondaryFields.firstOrNull()?.let {
                    SharedText(it, animatedVisibilityScope)
                }
            }
            // Additional fields and image
            AnimatedVisibility(detailed) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            for (i in 1..secondaryFields.lastIndex) {
                                SharedText(secondaryFields[i], animatedVisibilityScope)
                            }
                        }
                        ArtworkPreview(artwork)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
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

@Composable
private fun SharedTransitionScope.SharedText(
    field: SharedField,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Text(
        text = field.text,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(field.key),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    )
}

private val notFoundSize = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .height(40.dp)

@Composable
private fun NotFoundPlaceholder(
    id: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        HorizontalDivider()
        var showDetails by rememberSaveable { mutableStateOf(false) }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .clickable { showDetails = !showDetails }
                .then(notFoundSize)
        ) {
            Text(
                text = stringResource(R.string.not_found_title_template).format(id),
                fontStyle = FontStyle.Italic,
            )
        }
        AnimatedVisibility(showDetails) {
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

private val previewSize = Modifier
    .padding(start = 8.dp)
    .requiredSize(150.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtworkPreview(
    artwork: Artwork,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(previewSize)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .semantics(mergeDescendants = true) {}
    ) {
        if (artwork.images.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.no_image),
                    textAlign = TextAlign.Center,
                )
                if (!artwork.isPublicDomain) {
                    Text(
                        text = stringResource(R.string.not_in_public_domain),
                        textAlign = TextAlign.Center,
                    )
                    val tooltipState = rememberTooltipState()
                    val scope = rememberCoroutineScope()
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text(stringResource(R.string.not_in_public_domain_tooltip)) }
                        },
                        state = tooltipState,
                    ) {
                        IconButton({ scope.launch { tooltipState.show() } }) {
                            Icon(Icons.Default.Info, stringResource(R.string.show_additional_info))
                        }
                    }
                }
            }
        } else {
            val imageState = rememberAsyncImageState()
            val sharedTransitionScope = LocalSharedTransitionScope.current
                ?: throw IllegalStateException("No SharedTransitionScope found")
            val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
                ?: throw IllegalStateException("No AnimatedVisibilityScope found")
            with(sharedTransitionScope) {
                AsyncImage(
                    uri = artwork.images.first().previewUrl,
                    state = imageState,
                    contentDescription = stringResource(R.string.artwork_preview),
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(
                                SharedKey(artwork.id, SharedElementType.Image)
                            ),
                            animatedVisibilityScope,
                            resizeMode = RemeasureToBounds,
                        )
                        .fillMaxSize()
                )
            }
            when (imageState.loadState) {
                is SketchLoadState.Started -> {
                    CircularProgressIndicator(Modifier.zIndex(1f))
                }

                is SketchLoadState.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { imageState.restart() }
                            .padding(8.dp)
                            .zIndex(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.reload_failed_image),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // Success, Canceled, null
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtworkWithoutPreviewPreview() {
    val artwork = createArtwork(title = "Short title")
    var detailedView by remember { mutableStateOf(false) }
    Artwork(artwork, { detailedView = !detailedView }, detailedView, {}, {})
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
    Artwork(artwork, { detailedView = !detailedView }, detailedView, {}, {})
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
    NotFoundPlaceholder(12)
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    val items: List<ArtworkInfo> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreen(
        uiState = SearchResultUiState.Error,
        pagingArtworks = pagingItems,
        onAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    val items: List<ArtworkInfo> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreen(
        uiState = SearchResultUiState.Loading,
        pagingArtworks = pagingItems,
        onAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SuccessScreenPreview() {
    val items: List<ArtworkInfo> = List(20) { i ->
        createArtwork(id = i, title = "Artwork №$i", culture = "Culture", date = "Date")
    }
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreen(
        uiState = SearchResultUiState.Success(items.size),
        pagingArtworks = pagingItems,
        onAction = {},
    )
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
