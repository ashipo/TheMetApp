package com.ashipo.metropolitanmuseum.ui.artwork

import cafe.adriel.voyager.core.model.ScreenModel
import com.ashipo.metropolitanmuseum.data.getImagePreviewUrl
import com.ashipo.metropolitanmuseum.data.network.model.Artwork

class ArtworkScreenModel(
    artwork: Artwork,
) : ScreenModel {

    val uiState: ArtworkScreenState = ArtworkScreenState(
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

private fun getTitle(artwork: Artwork) =
    artwork.title.ifBlank {
        artwork.objectName
    }

private fun getConstituents(artwork: Artwork): List<ConstituentInfo> {
    return artwork.constituents?.map {
        ConstituentInfo(it.role, it.name)
    } ?: emptyList()
}

private fun getImages(artwork: Artwork): List<ArtworkImage> {
    if (artwork.primaryImageUrl.isBlank()) {
        return emptyList()
    }
    val images = mutableListOf<ArtworkImage>()
    images.add(ArtworkImage(artwork.primaryImagePreviewUrl, artwork.primaryImageUrl))
    if (artwork.additionalImagesUrls.isNullOrEmpty()) {
        return images
    }
    for (url in artwork.additionalImagesUrls) {
        val previewUrl = getImagePreviewUrl(url)
        images.add(ArtworkImage(previewUrl, url))
    }
    return images
}

private fun getGeography(artwork: Artwork): String {
    val location = buildList {
        if (artwork.city.isNotBlank()) {
            add(artwork.city)
        }
        if (artwork.state.isNotBlank()) {
            add(artwork.state)
        }
        if (artwork.county.isNotBlank()) {
            add(artwork.county)
        }
        if (artwork.country.isNotBlank()) {
            add(artwork.country)
        }
        if (artwork.region.isNotBlank()) {
            add(artwork.region)
        }
        if (artwork.subregion.isNotBlank()) {
            add(artwork.subregion)
        }
        if (artwork.locale.isNotBlank()) {
            add(artwork.locale)
        }
        if (artwork.locus.isNotBlank()) {
            add(artwork.locus)
        }
        if (artwork.excavation.isNotBlank()) {
            add(artwork.excavation)
        }
        if (artwork.river.isNotBlank()) {
            add(artwork.river)
        }
    }.joinToString(", ")
    return if (location.isBlank() || artwork.geographyType.isBlank()) {
        location
    } else {
        "${artwork.geographyType} $location"
    }
}

private fun getTags(artwork: Artwork) =
    artwork.tags?.map { it.term } ?: emptyList()

data class ArtworkScreenState(
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

data class ArtworkImage(val previewUrl: String, val imageUrl: String)

data class ConstituentInfo(val role: String, val info: String)
