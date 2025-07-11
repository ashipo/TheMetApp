package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.TestNetworkDataSource
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.ui.model.toLocal
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

@DisplayName("ArtworksRepository")
class ArtworksRepositoryTest {

    private lateinit var dataSource: TestNetworkDataSource
    private lateinit var subject: ArtworksRepository

    @BeforeEach
    fun setup() {
        dataSource = TestNetworkDataSource()
        subject = ArtworksRepository(dataSource)
    }

    @Test
    fun `getArtworks is backed by NetworkDataSource`() = runTest {
        val artwork = NetworkArtwork(id = 1, title = "Clock", objectPageUrl = "")
        val notFound = ArtworkRequestResult.NotFound(id = 2)
        dataSource.addArtworkResult(artwork)
        dataSource.addArtworkResult(notFound)

        val actual = subject.getArtworks(listOf(1, 2))

        assertContains(actual, artwork.toLocal())
        assertContains(actual, notFound.toLocal())
    }
}
