package com.ashipo.metropolitanmuseum.ui.searchresult

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.testing.asPagingSourceFactory
import com.ashipo.metropolitanmuseum.data.network.PAGE_SIZE
import com.ashipo.metropolitanmuseum.ui.model.Artwork
import com.ashipo.metropolitanmuseum.ui.model.ArtworkInfo
import com.ashipo.metropolitanmuseum.ui.model.Constituent
import com.ashipo.metropolitanmuseum.ui.util.SharedScopes
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val artworks = List(100) { i ->
        val id = i + 1000
        Artwork(
            id = id,
            title = "Artwork #$id",
        )
    }

    private val items = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = (artworks as List<ArtworkInfo>).asPagingSourceFactory(),
    ).flow

    @Test
    fun backButton_onClick_executesGoBackAction() {
        var actual: SearchResultScreenAction? = null
        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    SearchResultUiState.Error,
                    pagingArtworks = items,
                    onAction = { actual = it },
                )
            }
        }

        composeTestRule.onNodeWithTag("navigateBack").performClick()

        assertEquals(SearchResultScreenAction.GoBack, actual)
    }

    @Test
    fun showArtworkButton_onClick_executesShowArtworkAction() {
        val artwork = artworks[1]
        var actual: SearchResultScreenAction? = null
        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = { actual = it },
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText(artwork.title).performClick()
            onNodeWithTag("showArtwork").performClick()
        }

        assertEquals(SearchResultScreenAction.ShowArtwork(artwork), actual)
    }

    @Test
    fun showWebpageButton_onClick_executesOpenWebpageAction() {
        val artwork = artworks[1]
        var actual: SearchResultScreenAction? = null
        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = { actual = it },
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText(artwork.title).performClick()
            onNodeWithTag("openWebpage").performClick()
        }

        assertEquals(SearchResultScreenAction.OpenWebpage(artwork), actual)
    }

    @Test
    fun artworks_displayTitle() {
        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    uiState = SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText(artworks[0].title, true).assertIsDisplayed()
    }

    @Test
    fun artworkTitle_htmlIsConverted() {
        val artworks = listOf(
            Artwork(
                id = 1,
                title = "Artwork&#39;s <i>Italic</i>",
            )
        )
        val items = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = (artworks as List<ArtworkInfo>).asPagingSourceFactory(),
        ).flow

        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    uiState = SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = {},
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText("Italic", true).assertExists()
            onNodeWithText("'", true).assertExists()
            onNodeWithText("&#39;", true).assertDoesNotExist()
            onNodeWithText("<i>", true).assertDoesNotExist()
        }
    }

    @Test
    fun artworkArtist_htmlIsConverted() {
        val artworks = listOf(
            Artwork(
                id = 1,
                title = "Title",
                artistName = "<b>Mr.</b>.&#x20AC;",
            )
        )
        val items = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = (artworks as List<ArtworkInfo>).asPagingSourceFactory(),
        ).flow

        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    uiState = SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = {},
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText("Mr.", true).assertExists()
            onNodeWithText("€", true).assertExists()
            onNodeWithText("&#x20AC;", true).assertDoesNotExist()
            onNodeWithText("<b>", true).assertDoesNotExist()
        }
    }

    @Test
    fun artworkDate_htmlIsConverted() {
        val artworks = listOf(
            Artwork(
                id = 1,
                title = "Title",
                date = "Apostrophe&#39;s <i>Italic</i>",
            )
        )
        val items = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = (artworks as List<ArtworkInfo>).asPagingSourceFactory(),
        ).flow

        composeTestRule.setContent {
            SharedScopes {
                val items = items.collectAsLazyPagingItems()
                SearchResultScreen(
                    uiState = SearchResultUiState.Success(artworks.size),
                    pagingArtworks = items,
                    onAction = {},
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText("Italic", true).assertExists()
            onNodeWithText("'", true).assertExists()
            onNodeWithText("&#39;", true).assertDoesNotExist()
            onNodeWithText("<i>", true).assertDoesNotExist()
        }
    }
}
