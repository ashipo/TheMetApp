package com.ashipo.metropolitanmuseum.ui.model

import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.data.network.model.NetworkConstituent
import com.ashipo.metropolitanmuseum.data.network.model.NetworkTag
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Artwork")
class ArtworkTest {

    @Nested
    @DisplayName("NetworkArtwork.toLocal()")
    inner class ToLocal {

        @Test
        @DisplayName("Artwork.title - when NetworkArtwork.title is blank - is backed by NetworkArtwork.objectName")
        fun `Artwork_title - when NetworkArtwork_title is blank - is backed by NetworkArtwork_objectName`() {
            val expected = "Title"
            val networkArtwork = NetworkArtwork(id = 1, title = "", objectName = expected)

            val actual = networkArtwork.toLocal().title

            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("Artwork.geography - backed up by geography-related NetworkArtwork fields")
        fun `Artwork_geography backed by NetworkArtwork fields`() {
            val networkArtwork = NetworkArtwork(
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

            val geography = networkArtwork.toLocal().geography

            assertNotNull(geography)
            assertTrue(geography.contains("Amsterdam"))
            assertTrue(geography.contains("Rajasthan"))
            assertTrue(geography.contains("Lincolnshire"))
            assertTrue(geography.contains("East Timor"))
            assertTrue(geography.contains("Northern"))
            assertTrue(geography.contains("Deir el-Bahri"))
            assertTrue(geography.contains("Temple of Hatshepsut"))
            assertTrue(geography.contains("Pit 477"))
            assertTrue(geography.contains("MMA excavations, 1923–24"))
            assertTrue(geography.contains("Volga river"))
        }

        @Test
        @DisplayName("Artwork.constituents - backed up by NetworkArtwork.constituents")
        fun `Artwork_constituents backed by NetworkArtwork_constituents`() {
            val networkConstituents = List(3) { i ->
                NetworkConstituent(
                    id = i,
                    role = "role $i",
                    name = "name $i",
                    ulanUrl = "",
                    wikidataUrl = "",
                    gender = "",
                )
            }
            val networkArtwork = NetworkArtwork(id = 12, constituents = networkConstituents)

            val constituents = networkArtwork.toLocal().constituents

            assertEquals(networkConstituents.size, constituents.size)
            for (i in constituents.indices) {
                val networkConstituent = networkConstituents[i]
                val constituent = constituents[i]
                assertTrue(constituent.role.contains(networkConstituent.role))
                assertTrue(constituent.name.contains(networkConstituent.name))
            }
        }

        @Test
        @DisplayName("Artwork.constituents - when NetworkArtwork.constituents is null - are empty")
        fun `Artwork_constituents - when NetworkArtwork_constituents is null - are empty`() {
            val networkArtwork = NetworkArtwork(id = 12, constituents = null)

            val actual = networkArtwork.toLocal().constituents

            assertTrue { actual.isEmpty() }
        }

        @Test
        @DisplayName("Artwork.tags - backed up by NetworkArtwork.tags")
        fun `Artwork_tags - backed up by NetworkArtwork_tags`() {
            val networkTags = listOf(
                NetworkTag("Portrait", "www.aat.url/1", "www.wiki.url/1"),
                NetworkTag("Painting", "www.aat.url/2", "www.wiki.url/2"),
            )
            val networkArtwork = NetworkArtwork(id = 12, tags = networkTags)
            val expected = networkTags.map { it.term }

            val actual = networkArtwork.toLocal().tags

            assertContentEquals(expected, actual)
        }

        @Test
        @DisplayName("Artwork.tags - when NetworkArtwork.tags is null - are empty")
        fun `Artwork_tags - when NetworkArtwork_tags is null - are empty`() {
            val networkArtwork = NetworkArtwork(id = 12, tags = null)

            val actual = networkArtwork.toLocal().tags

            assertTrue { actual.isEmpty() }
        }

        @Test
        @DisplayName("Artwork.images - when NetworkArtwork.primaryImage is blank - is empty")
        fun `Artwork_images - when NetworkArtwork_primaryImage is blank - is empty`() {
            val networkArtwork = NetworkArtwork(
                id = 1,
                primaryImageUrl = "  ",
            )

            val actual = networkArtwork.toLocal().images

            assertTrue { actual.isEmpty() }
        }

        @Test
        @DisplayName("Artwork.images - backed up by images-related NetworkArtwork fields")
        fun `Artwork_images - backed up by images-related NetworkArtwork fields`() {
            val primaryImage =
                "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-001.jpg"
            val primaryImagePreview =
                "https://images.metmuseum.org/CRDImages/gr/web-large/DP-18928-001.jpg"
            val additionalImages = listOf(
                "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-002.jpg",
                "https://images.metmuseum.org/CRDImages/gr/original/DP-18928-003.jpg",
            )
            val networkArtwork = NetworkArtwork(
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

            val actual = networkArtwork.toLocal().images

            assertEquals(expected, actual)
        }
    }
}
