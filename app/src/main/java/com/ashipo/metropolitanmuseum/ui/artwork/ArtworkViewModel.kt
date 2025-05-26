package com.ashipo.metropolitanmuseum.ui.artwork

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.ui.artwork.navigation.ArtworkRoute
import com.ashipo.metropolitanmuseum.ui.artwork.navigation.ArtworkType
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import kotlin.reflect.typeOf

class ArtworkViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artwork: Artwork = savedStateHandle.toRoute<ArtworkRoute>(
        typeMap = mapOf(typeOf<Artwork>() to ArtworkType)
    ).artwork

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
    val primaryLarge = getLargeImageUrl(artwork.primaryImageUrl)
    val primaryPreview = artwork.primaryImagePreviewUrl
    images.add(ArtworkImage(artwork.primaryImageUrl, primaryLarge, primaryPreview))

    if (artwork.additionalImagesUrls.isNullOrEmpty()) {
        return images
    }
    for (original in artwork.additionalImagesUrls) {
        val preview = getPreviewImageUrl(original)
        val large = getLargeImageUrl(original)
        images.add(ArtworkImage(original, large, preview))
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

data class ConstituentInfo(val role: String, val info: String)
