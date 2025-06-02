package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkDetailRoute
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkType
import com.ashipo.metropolitanmuseum.ui.model.Artwork
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.reflect.typeOf
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ArtworkDetailViewModelTest {

    private val typeMap = mapOf(typeOf<Artwork>() to ArtworkType)

    @Test
    fun `uiState_id - backed up by Artwork_id`() {
        val expected = 123
        val artwork = Artwork(
            id = expected,
            title = "title",
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.id

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_title - backed up by Artwork_title`() {
        val expected = "Jaguar Whistling Jar"
        val artwork = Artwork(
            id = 1,
            title = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.title

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_period - backed up by Artwork_period`() {
        val expected = "Late 90's"
        val artwork = Artwork(
            id = 1,
            title = "title",
            period = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.period

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_date - backed up by Artwork_date`() {
        val expected = "Late 90's"
        val artwork = Artwork(
            id = 1,
            title = "title",
            date = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.date

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_geography - backed up by Artwork_geography`() {
        val expected = "Arctica, Iceberg"
        val artwork = Artwork(
            id = 1,
            title = "title",
            geography = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.geography

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_culture - backed up by Artwork_culture`() {
        val expected = "Persian"
        val artwork = Artwork(
            id = 1,
            title = "title",
            culture = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.culture

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_medium - backed up by Artwork_medium`() {
        val expected = "Wood"
        val artwork = Artwork(
            id = 1,
            title = "title",
            medium = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.medium

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_classification - backed up by Artwork_classification`() {
        val expected = "Sculpture"
        val artwork = Artwork(
            id = 1,
            title = "title",
            classification = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.classification

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_department - backed up by Artwork_department`() {
        val expected = "Egyptian Art"
        val artwork = Artwork(
            id = 1,
            title = "title",
            department = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.department

        assertEquals(expected, actual)
    }
}
