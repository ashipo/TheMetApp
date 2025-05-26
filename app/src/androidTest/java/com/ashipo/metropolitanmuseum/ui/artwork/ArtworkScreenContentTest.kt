package com.ashipo.metropolitanmuseum.ui.artwork

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class ArtworkScreenContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun backButton_onClick_navigatesBack() {
        var actual = false
        composeTestRule.setContent {
            ArtworkScreenContent(
                ArtworkScreenState(),
                { actual = true },
                {},
            )
        }

        composeTestRule.onNodeWithTag("navigateBack").performClick()

        assertTrue(actual)
    }

    @Test
    fun artworkInfo_isDisplayed() {
        // Tags are not displayed currently
        val state = ArtworkScreenState(
            title = "Chewbacca's bowcaster",
            constituents = listOf(
                ConstituentInfo("The Emperor", "Palpatine"),
                ConstituentInfo("Jedi Master", "Luke Skywalker"),
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
            ArtworkScreenContent(state, {}, {})
        }

        composeTestRule.apply {
            onNodeWithText(state.title, true).assertExists()
            for (constituent in state.constituents) {
                composeTestRule.onNodeWithText(constituent.role, true).assertExists()
                composeTestRule.onNodeWithText(constituent.info, true).assertExists()
            }
            onNodeWithText(state.period, true).assertExists()
            onNodeWithText(state.date, true).assertExists()
            onNodeWithText(state.geography, true).assertExists()
            onNodeWithText(state.culture, true).assertExists()
            onNodeWithText(state.medium, true).assertExists()
            onNodeWithText(state.classification, true).assertExists()
            onNodeWithText(state.department, true).assertExists()
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
            ArtworkScreenContent(
                ArtworkScreenState(images = images), {}, {})
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
    fun mainImage_onClick_displaysFullScreen() {
        val images = List(10) { i ->
            ArtworkImage(
                originalUrl = "https://example.com/image_$i.jpg",
                previewUrl = "https://example.com/preview_$i.jpg",
                largeUrl = "https://example.com/large_$i.jpg",
            )
        }
        val expectedIndex = images.lastIndex
        var actualIndex: Int? = null
        composeTestRule.setContent {
            ArtworkScreenContent(
                ArtworkScreenState(images = images), {}, { actualIndex = it })
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

        assertEquals(expectedIndex, actualIndex)
    }
}
