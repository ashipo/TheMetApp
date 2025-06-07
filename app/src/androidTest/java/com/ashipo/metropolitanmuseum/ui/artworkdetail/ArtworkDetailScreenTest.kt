@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailScreenAction.GoBack
import com.ashipo.metropolitanmuseum.ui.artworkdetail.ArtworkDetailScreenAction.ShowImages
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.ashipo.metropolitanmuseum.ui.model.Constituent
import com.ashipo.metropolitanmuseum.ui.util.SharedScopes
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class ArtworkDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun backButton_onClick_executesGoBackAction() {
        var actualAction: ArtworkDetailScreenAction? = null
        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(
                    ArtworkDetailScreenState(id = 1, title = "Title"),
                    { actualAction = it },
                )
            }
        }

        composeTestRule.onNodeWithTag("navigateBack").performClick()

        assertEquals(GoBack, actualAction)
    }

    @Test
    fun artworkInfo_isDisplayed() {
        // Tags are not displayed currently
        val state = ArtworkDetailScreenState(
            id = 123,
            title = "Chewbacca's bowcaster",
            constituents = listOf(
                Constituent("The Emperor", "Palpatine"),
                Constituent("Jedi Master", "Luke Skywalker"),
            ),
            period = "A long time ago",
            date = "32 BBY - 35 ABY",
            geography = "A galaxy far, far away",
            culture = "Wookiee",
            medium = "Kshyyy-vine, Steel",
            classification = "Weapon",
            department = "Kashyyyk",
        )

        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(state, {})
            }
        }

        composeTestRule.apply {
            onNodeWithText(state.title, true).assertExists()
            for (constituent in state.constituents) {
                composeTestRule.onNodeWithText(constituent.role, true).assertExists()
                composeTestRule.onNodeWithText(constituent.name, true).assertExists()
            }
            onNodeWithText(state.period!!, true).assertExists()
            onNodeWithText(state.date!!, true).assertExists()
            onNodeWithText(state.geography!!, true).assertExists()
            onNodeWithText(state.culture!!, true).assertExists()
            onNodeWithText(state.medium!!, true).assertExists()
            onNodeWithText(state.classification!!, true).assertExists()
            onNodeWithText(state.department!!, true).assertExists()
        }
    }

    @Test
    fun mainImageAndPreviews_whenArtworkWithImages_exist() {
        val images = List(20) { i ->
            ArtworkImage(
                originalUrl = "https://example.com/image_$i.jpg",
                previewUrl = "https://example.com/preview_$i.jpg",
                largeUrl = "https://example.com/large_$i.jpg",
            )
        }

        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(
                    ArtworkDetailScreenState(1, "Title", images = images), {})
            }
        }

        composeTestRule.apply {
            // Main image
            onNodeWithTag("image", true).assertExists()
            for (i in images.indices) {
                // Scroll to a preview (will throw if there is no matching node)
                onNodeWithTag("previews").performScrollToNode(hasTestTag("preview:$i"))
            }
        }
    }

    @Test
    fun mainImage_onClick_executesShowImagesAction() {
        val images = List(10) { i ->
            ArtworkImage(
                originalUrl = "https://example.com/image_$i.jpg",
                previewUrl = "https://example.com/preview_$i.jpg",
                largeUrl = "https://example.com/large_$i.jpg",
            )
        }
        val expectedIndex = images.lastIndex
        var actualAction: ArtworkDetailScreenAction? = null
        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(
                    ArtworkDetailScreenState(1, "Title", images = images), { actualAction = it })
            }
        }

        composeTestRule.apply {
            val tag = "preview:$expectedIndex"
            // Scroll to a preview
            onNodeWithTag("previews").performScrollToNode(hasTestTag(tag))
            // Click the preview
            onNodeWithTag(tag).performClick()
            // Click the main image
            onNodeWithTag("image", true).performClick()
        }

        assert(actualAction is ShowImages)
        assertEquals(expectedIndex, (actualAction as ShowImages).initialImageIndex)
    }

    @Test
    fun title_htmlIsConverted() {
        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(
                    ArtworkDetailScreenState(1, "Artwork&#39;s <i>Italic</i>"),
                    {},
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
    fun constituents_htmlIsConverted() {
        composeTestRule.setContent {
            SharedScopes {
                ArtworkDetailScreen(
                    ArtworkDetailScreenState(
                        id = 1,
                        title = "title",
                        constituents = listOf(Constituent("Artist", "<b>Mr.</b>.&#x20AC;")),
                    ),
                    {},
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
}
