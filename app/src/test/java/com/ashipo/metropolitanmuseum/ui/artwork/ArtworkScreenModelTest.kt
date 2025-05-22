package com.ashipo.metropolitanmuseum.ui.artwork

import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.Constituent
import com.ashipo.metropolitanmuseum.data.network.model.Tag
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import kotlin.test.assertEquals

class ArtworkScreenModelTest {

    @Test
    @DisplayName("uiState.id - same as Artwork.id")
    fun `uiState_id - is same as Artwork_id`() {
        val expected = 123
        val artwork = Artwork(
            id = expected,
        )

        val model = ArtworkScreenModel(artwork)
        val actual = model.uiState.id

        assertEquals(expected, actual)
    }

    @Nested
    @DisplayName("uiState.title")
    inner class Title {

        @Test
        @DisplayName("when Artwork.title is not blank - same as Artwork.title")
        fun `when Artwork_title is not blank - Artwork_title is used`() {
            val expected = "Jaguar Whistling Jar"
            val artwork = Artwork(
                id = 1,
                title = expected,
            )

            val model = ArtworkScreenModel(artwork)
            val actual = model.uiState.title

            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("when Artwork.title is blank - same as Artwork.objectName")
        // Example id: 503542
        fun `when Artwork_title is blank - Artwork_objectName is used`() {
            val expected = "Jaguar Whistling Jar"
            val artwork = Artwork(
                id = 1,
                title = "",
                objectName = expected,
            )

            val model = ArtworkScreenModel(artwork)
            val actual = model.uiState.title

            assertEquals(expected, actual)
        }
    }

    @Test
    @DisplayName("uiState.constituents - contains data from Artwork.constituents")
    fun `uiState_constituents - contains data from Artwork_constituents`() {
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

        val actual = ArtworkScreenModel(artwork).uiState.constituents

        assertEquals(constituents.size, actual.size)
        for (i in constituents.indices) {
            val constituent = constituents[i]
            val actualInfo = actual[i]
            assertTrue(actualInfo.role.contains(constituent.role))
            assertTrue(actualInfo.info.contains(constituent.name))
        }
    }

    @Test
    @DisplayName("uiState.period - same as Artwork.period")
    fun `uiState_period - same as Artwork_period`() {
        val expected = "Late 90's"
        val artwork = Artwork(
            id = 1,
            period = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.period

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("uiState.date - same as Artwork.date")
    fun `uiState_date - same as Artwork_date`() {
        val expected = "Late 90's"
        val artwork = Artwork(
            id = 1,
            date = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.date

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("uiState.geography - contains data from geography-related Artwork fields")
    fun `uiState_geography - contains data from geography-related Artwork fields`() {
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

        val actual = ArtworkScreenModel(artwork).uiState.geography

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
    @DisplayName("uiState.culture - same as Artwork.culture")
    fun `uiState_culture - same as Artwork_culture`() {
        val expected = "Persian"
        val artwork = Artwork(
            id = 1,
            culture = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.culture

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("uiState.medium - same as Artwork.medium")
    fun `uiState_medium - same as Artwork_medium`() {
        val expected = "Wood"
        val artwork = Artwork(
            id = 1,
            medium = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.medium

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("uiState.classification - same as Artwork.classification")
    fun `uiState_classification - same as Artwork_classification`() {
        val expected = "Sculpture"
        val artwork = Artwork(
            id = 1,
            classification = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.classification

        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("uiState.department - ame as Artwork.department")
    fun `uiState_department - same as Artwork_department`() {
        val expected = "Egyptian Art"
        val artwork = Artwork(
            id = 1,
            department = expected,
        )

        val actual = ArtworkScreenModel(artwork).uiState.department

        assertEquals(expected, actual)
    }

    @Nested
    @DisplayName("uiState.images")
    inner class Images {

        @Test
        @DisplayName("when Artwork.primaryImage is blank - is empty")
        fun `when Artwork_primaryImage is blank - is empty`() {
            val artwork = Artwork(
                id = 1,
                primaryImageUrl = "  ",
            )

            val actual = ArtworkScreenModel(artwork).uiState.images

            assertTrue { actual.isEmpty() }
        }

        @Test
        @DisplayName("contains data from images-related Artwork fields")
        fun `contains data from images-related Artwork fields`() {
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

            val actual = ArtworkScreenModel(artwork).uiState.images

            assertEquals(expected, actual)
        }
    }

    @Nested
    @DisplayName("uiState.tags")
    inner class Tags {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("when Artwork.tags is null or empty - is empty")
        fun `when Artwork_tags is null or empty - is empty`(tags: List<Tag>?) {
            val artwork = Artwork(
                id = 1,
                tags = tags,
            )

            val actual = ArtworkScreenModel(artwork).uiState.tags

            assertTrue { actual.isEmpty() }
        }

        @Test
        @DisplayName("contains data from Artwork.tags ")
        fun `contains data from Artwork_tags`() {
            val tags = listOf(
                Tag("Portrait", "www.aat.url/1", "www.wiki.url/1"),
                Tag("Painting", "www.aat.url/2", "www.wiki.url/2"),
            )
            val artwork = Artwork(
                id = 1,
                tags = tags,
            )
            val expected = tags.map { it.term }

            val actual = ArtworkScreenModel(artwork).uiState.tags

            assertEquals(expected, actual)
        }
    }
}
