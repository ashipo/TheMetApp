package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkDetailRoute
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkType
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import kotlin.reflect.typeOf

class ArtworkDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artwork: NetworkArtwork = savedStateHandle.toRoute<ArtworkDetailRoute>(
        typeMap = mapOf(typeOf<NetworkArtwork>() to ArtworkType)
    ).artwork

    val uiState: ArtworkDetailScreenState = ArtworkDetailScreenState(
        id = artwork.id,
        title = getTitle(artwork),
        constituents = getConstituents(artwork),
        period = artwork.period,
        date = artwork.date,
        geography = getGeography(artwork),
        culture = artwork.culture,
        medium = artwork.medium,
        classification = artwork.classification,
        department = artwork.department,
        images = getImages(artwork),
        tags = getTags(artwork),
    )
}

private fun getTitle(networkArtwork: NetworkArtwork) =
    networkArtwork.title.ifBlank {
        networkArtwork.objectName
    }

private fun getConstituents(networkArtwork: NetworkArtwork): List<ConstituentInfo> {
    return networkArtwork.constituents?.map {
        ConstituentInfo(it.role, it.name)
    } ?: emptyList()
}

private fun getImages(networkArtwork: NetworkArtwork): List<ArtworkImage> {
    if (networkArtwork.primaryImageUrl.isBlank()) {
        return emptyList()
    }
    val images = mutableListOf<ArtworkImage>()
    val primaryLarge = getLargeImageUrl(networkArtwork.primaryImageUrl)
    val primaryPreview = networkArtwork.primaryImagePreviewUrl
    images.add(ArtworkImage(networkArtwork.primaryImageUrl, primaryLarge, primaryPreview))

    if (networkArtwork.additionalImagesUrls.isNullOrEmpty()) {
        return images
    }
    for (original in networkArtwork.additionalImagesUrls) {
        val preview = getPreviewImageUrl(original)
        val large = getLargeImageUrl(original)
        images.add(ArtworkImage(original, large, preview))
    }
    return images
}

private fun getGeography(networkArtwork: NetworkArtwork): String {
    val location = buildList {
        if (networkArtwork.city.isNotBlank()) {
            add(networkArtwork.city)
        }
        if (networkArtwork.state.isNotBlank()) {
            add(networkArtwork.state)
        }
        if (networkArtwork.county.isNotBlank()) {
            add(networkArtwork.county)
        }
        if (networkArtwork.country.isNotBlank()) {
            add(networkArtwork.country)
        }
        if (networkArtwork.region.isNotBlank()) {
            add(networkArtwork.region)
        }
        if (networkArtwork.subregion.isNotBlank()) {
            add(networkArtwork.subregion)
        }
        if (networkArtwork.locale.isNotBlank()) {
            add(networkArtwork.locale)
        }
        if (networkArtwork.locus.isNotBlank()) {
            add(networkArtwork.locus)
        }
        if (networkArtwork.excavation.isNotBlank()) {
            add(networkArtwork.excavation)
        }
        if (networkArtwork.river.isNotBlank()) {
            add(networkArtwork.river)
        }
    }.joinToString(", ")
    return if (location.isBlank() || networkArtwork.geographyType.isBlank()) {
        location
    } else {
        "${networkArtwork.geographyType} $location"
    }
}

private fun getTags(networkArtwork: NetworkArtwork) =
    networkArtwork.tags?.map { it.term } ?: emptyList()

data class ArtworkDetailScreenState(
    val id: Int = 0,
    val title: String = "",
    val constituents: List<ConstituentInfo> = emptyList(),
    val period: String = "",
    val date: String = "",
    val geography: String = "",
    val culture: String = "",
    val medium: String = "",
    val classification: String = "",
    val department: String = "",
    val images: List<ArtworkImage> = emptyList(),
    val tags: List<String> = emptyList(),
)

data class ConstituentInfo(val role: String, val info: String)
