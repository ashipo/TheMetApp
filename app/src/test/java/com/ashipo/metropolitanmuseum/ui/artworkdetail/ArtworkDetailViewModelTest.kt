package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.Constituent
import com.ashipo.metropolitanmuseum.data.network.model.Tag
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkDetailRoute
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkType
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ArtworkDetailViewModelTest {

    private val typeMap = mapOf(typeOf<Artwork>() to ArtworkType)

    @Test
    fun `uiState_id - backed up by Artwork_id`() {
        val expected = 123
        val artwork = Artwork(
            id = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.id

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_title - when Artwork_title is not blank - same as Artwork_title`() {
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
    // Example id: 503542
    fun `uiState_title - when Artwork_title is blank - same as Artwork_objectName`() {
        val expected = "Jaguar Whistling Jar"
        val artwork = Artwork(
            id = 1,
            title = "",
            objectName = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.title

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_constituents - backed up by Artwork_constituents`() {
        val constituents = List(3) { i ->
            Constituent(
                id = i,
                role = "role $i",
                name = "name $i",
                ulanUrl = "",
                wikidataUrl = "",
                gender = "",
            )
        }
        val artwork = Artwork(
            id = 1,
            constituents = constituents,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.constituents

        assertEquals(constituents.size, actual.size)
        for (i in constituents.indices) {
            val constituent = constituents[i]
            val actualInfo = actual[i]
            assertTrue(actualInfo.role.contains(constituent.role))
            assertTrue(actualInfo.info.contains(constituent.name))
        }
    }

    @Test
    fun `uiState_period - backed up by Artwork_period`() {
        val expected = "Late 90's"
        val artwork = Artwork(
            id = 1,
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
            date = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.date

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_geography - backed up by geography-related Artwork fields`() {
        val artwork = Artwork(
            id = 1,
            city = "Amsterdam",
            state = "Rajasthan",
            county = "Lincolnshire",
            country = "East Timor",
            region = "Northern",
            subregion = "Deir el-Bahri",
            locale = "Temple of Hatshepsut",
            locus = "Pit 477",
            excavation = "MMA excavations, 1923–24",
            river = "Volga river",
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.geography

        assertTrue(actual.contains("Amsterdam"))
        assertTrue(actual.contains("Rajasthan"))
        assertTrue(actual.contains("Lincolnshire"))
        assertTrue(actual.contains("East Timor"))
        assertTrue(actual.contains("Northern"))
        assertTrue(actual.contains("Deir el-Bahri"))
        assertTrue(actual.contains("Temple of Hatshepsut"))
        assertTrue(actual.contains("Pit 477"))
        assertTrue(actual.contains("MMA excavations, 1923–24"))
        assertTrue(actual.contains("Volga river"))
    }

    @Test
    fun `uiState_culture - backed up by Artwork_culture`() {
        val expected = "Persian"
        val artwork = Artwork(
            id = 1,
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
            department = expected,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.department

        assertEquals(expected, actual)
    }

    @Test
    fun `uiState_images - when Artwork_primaryImage is blank - is empty`() {
        val artwork = Artwork(
            id = 1,
            primaryImageUrl = "  ",
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.images

        assertTrue { actual.isEmpty() }
    }


    @Test
    fun `uiState_images - backed up by images-related Artwork fields`() {
        val primaryImage =
            "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-001.jpg"
        val primaryImagePreview = getPreviewImageUrl(primaryImage)
        val additionalImages = listOf(
            "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-002.jpg",
            "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-003.jpg",
        )
        val artwork = Artwork(
            id = 1,
            primaryImageUrl = primaryImage,
            primaryImagePreviewUrl = primaryImagePreview,
            additionalImagesUrls = additionalImages,
        )
        val expected = buildList {
            add(
                ArtworkImage(
                    primaryImage,
                    getLargeImageUrl(primaryImage),
                    getPreviewImageUrl(primaryImage),
                )
            )
            additionalImages.forEach { image ->
                add(
                    ArtworkImage(
                        image,
                        getLargeImageUrl(image),
                        getPreviewImageUrl(image),
                    )
                )
            }
        }

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.images

        assertEquals(expected, actual)
    }

    fun `uiState_tags - when Artwork_tags is null - is empty`() {
        val artwork = Artwork(
            id = 1,
            tags = null,
        )

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.tags

        assertTrue { actual.isEmpty() }
    }

    @Test
    fun `uiState_tags - backed up by Artwork_tags`() {
        val tags = listOf(
            Tag("Portrait", "www.aat.url/1", "www.wiki.url/1"),
            Tag("Painting", "www.aat.url/2", "www.wiki.url/2"),
        )
        val artwork = Artwork(
            id = 1,
            tags = tags,
        )
        val expected = tags.map { it.term }

        val model = ArtworkDetailViewModel(SavedStateHandle(ArtworkDetailRoute(artwork), typeMap))
        val actual = model.uiState.tags

        assertEquals(expected, actual)
    }
}
