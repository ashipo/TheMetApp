package com.ashipo.metropolitanmuseum.ui.artwork

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.annotation.ExperimentalCoilApi
import coil3.test.FakeImageLoaderEngine
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
                { actual = true }
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
            ArtworkScreenContent(state, {})
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
    fun mainImageAndPreviews_whenArtworkWithImages_areDisplayed() {
        setupCoil()
        val images = List(2) { i ->
            ArtworkImage(
                previewUrl = "https://example.com/preview_$i.jpg",
                imageUrl = "https://example.com/image_$i.jpg",
            )
        }

        composeTestRule.setContent {
            ArtworkScreenContent(
                ArtworkScreenState(images = images),
                {},
            )
        }

        composeTestRule.apply {
            // Main image
            onNodeWithTag("image:${images[0].imageUrl}", true).assertIsDisplayed()
            for (image in images) {
                // Previews
                onNodeWithTag("preview:${image.previewUrl}").assertIsDisplayed()
            }
        }
    }

    @Test
    fun mainImage_onClick_displaysFullScreen() {
        setupCoil()
        val images = List(2) { i ->
            ArtworkImage(
                previewUrl = "https://example.com/preview_$i.jpg",
                imageUrl = "https://example.com/image_$i.jpg",
            )
        }

        composeTestRule.setContent {
            ArtworkScreenContent(
                ArtworkScreenState(images = images),
                {},
            )
        }

        composeTestRule.apply {
            val url = images[0].imageUrl
            onNodeWithTag("image:$url", true).performClick()
            onNodeWithTag("imageFullScreen:$url", true).assertIsDisplayed()
        }
    }

    @OptIn(DelicateCoilApi::class, ExperimentalCoilApi::class)
    private fun setupCoil() {
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorImage(Color.Blue.toArgb()))
            .build()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val imageLoader = ImageLoader.Builder(appContext)
            .components { add(engine) }
            .build()
        SingletonImageLoader.setUnsafe(imageLoader)
    }
}
