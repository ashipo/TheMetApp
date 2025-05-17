package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.rememberAsyncImagePainter
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import com.ashipo.metropolitanmuseum.ui.util.openUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreenContent(
    uiState: SearchResultUiState,
    pagingArtworks: LazyPagingItems<ArtworkResult>,
    onAction: (SearchResultScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
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
                        onClick = { onAction(SearchResultScreenAction.GoBack) },
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
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)

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
                    Column {
                        pagingArtworks[index]?.let { artworkResult ->
                            when (artworkResult) {
                                is Artwork -> Artwork(
                                    artwork = artworkResult,
                                    onShowArtwork = {
                                        onAction(SearchResultScreenAction.ShowArtwork(artworkResult))
                                    },
                                    onOpenWebpage = {
                                        onAction(SearchResultScreenAction.OpenWebpage(artworkResult))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                is ArtworkResult.NotFound -> NotFoundPlaceholder(
                                    artworkResult.id,
                                    Modifier.fillMaxWidth()
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

@Composable
private fun Artwork(
    artwork: Artwork,
    onShowArtwork: () -> Unit,
    onOpenWebpage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryInfo = artwork.title.ifBlank {
        artwork.objectName
    }
    val secondaryInfo = remember(artwork) {
        buildList {
            if (artwork.artistName.isNotBlank()) {
                add(artwork.artistName)
            }
            if (artwork.culture.isNotBlank()) {
                add(artwork.culture)
            }
            if (artwork.period.isNotBlank()) {
                add(artwork.period)
            }
            if (artwork.date.isNotBlank()) {
                add(artwork.date)
            }
            if (artwork.classification.isNotBlank()) {
                add(artwork.classification)
            }
        }
    }
    Column(modifier.testTag("artwork:${artwork.id}")) {
        HorizontalDivider()
        var showDetails by rememberSaveable { mutableStateOf(false) }
        // Clickable top part with the title and a piece of additional info
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable { showDetails = !showDetails }
                .then(artworkSize)
        ) {
            Text(
                text = primaryInfo,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            secondaryInfo.firstOrNull()?.let { text ->
                Text(
                    text = text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        AnimatedVisibility(showDetails) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        for (i in 1..secondaryInfo.lastIndex) {
                            Text(secondaryInfo[i])
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .then(previewSize)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .semantics(mergeDescendants = true) {}
    ) {
        if (artwork.primaryImagePreviewUrl.isBlank()) {
            Text(stringResource(R.string.no_preview))
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
        } else {
            val painter = rememberAsyncImagePainter(artwork.primaryImagePreviewUrl)
            val painterState by painter.state.collectAsState()
            when (painterState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.artwork_preview),
                        modifier = Modifier.fillMaxSize()
                    )
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
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtworkWithoutPreviewPreview() {
    ArtworkPreview(title = "Short title", artistName = "Artist name")
}

@Preview(showBackground = true)
@Composable
private fun ArtworkErrorLoadingPreviewPreview() {
    ArtworkPreview(
        previewUrl = "https:www.test.com"
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
// Doesn't work, painter.state is always Empty and preview is in loading animation
private fun ArtworkWithPreviewPreview() {
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(color = Color.Cyan.toArgb(), width = 500, height = 500)
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        ArtworkPreview(
            previewUrl = "www.test.com/image.jpg"
        )
    }
}

@Composable
private fun ArtworkPreview(
    id: Int = 880055,
    isHighlight: Boolean = true,
    isPublicDomain: Boolean = true,
    department: String = "Egyptian Art",
    objectName: String = "Object name",
    title: String = "Great Pyramid of Giza ".repeat(20),
    culture: String = "B ".repeat(255),
    period: String = "Old Kingdom Old Kingdom Old 1 2 3 4 5 6 7 8 9 0 1 2 3 4",
    artistPrefix: String = "prefix",
    artistName: String = "Picasso ".repeat(20),
    artistRole: String = "Painter",
    artistBio: String = "One of the artists of all times",
    artistSuffix: String = "suffix",
    date: String = "2600 BC",
    medium: String = "Oil on canvas",
    classification: String = "Pyramid",
    previewUrl: String = "",
    objectPageUrl: String = "https://www.metmuseum.org/art/collection/search/753523",
) {
    val artwork = Artwork(
        id = id,
        isHighlight = isHighlight,
        isPublicDomain = isPublicDomain,
        department = department,
        objectName = objectName,
        title = title,
        culture = culture,
        period = period,
        constituents = emptyList(),
        artistPrefix = artistPrefix,
        artistName = artistName,
        artistRole = artistRole,
        artistBio = artistBio,
        artistSuffix = artistSuffix,
        date = date,
        medium = medium,
        geographyType = "",
        city = "",
        state = "",
        county = "",
        country = "",
        region = "",
        subregion = "",
        locale = "",
        locus = "",
        excavation = "",
        river = "",
        classification = classification,
        primaryImagePreviewUrl = previewUrl,
        primaryImageUrl = "",
        additionalImagesUrls = emptyList(),
        objectPageUrl = objectPageUrl,
        tags = emptyList(),
    )
    Artwork(artwork, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun NotFoundPlaceholderPreview() {
    NotFoundPlaceholder(12)
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    val items: List<ArtworkResult> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreenContent(
        uiState = SearchResultUiState.Error,
        pagingArtworks = pagingItems,
        onAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    val items: List<ArtworkResult> = emptyList()
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreenContent(
        uiState = SearchResultUiState.Loading,
        pagingArtworks = pagingItems,
        onAction = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SuccessScreenPreview() {
    val items: List<ArtworkResult> = List(20) { i ->
        Artwork(id = i, title = "Artwork №$i", culture = "Culture", date = "Date")
    }
    val pagingItems = MutableStateFlow(PagingData.from(items)).collectAsLazyPagingItems()
    SearchResultScreenContent(
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
            Modifier.fillMaxWidth().height(56.dp)
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
            Modifier.fillMaxWidth().height(56.dp)
        )
    }
}
