package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.data.network.model.ArtworkResult
import com.ashipo.metropolitanmuseum.data.network.TestNetworkDataSource
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
        val artwork = Artwork(id = 1)
        val notFound = ArtworkResult.NotFound(id = 2)
        dataSource.addArtworkResult(artwork)
        dataSource.addArtworkResult(notFound)

        val actual = subject.getArtworks(listOf(1, 2))

        assertContains(actual, artwork)
        assertContains(actual, ArtworkResult.NotFound(2))
    }
}
