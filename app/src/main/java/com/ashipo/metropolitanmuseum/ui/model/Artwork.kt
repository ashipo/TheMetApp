package com.ashipo.metropolitanmuseum.ui.model

import com.ashipo.metropolitanmuseum.data.getLargeImageUrl
import com.ashipo.metropolitanmuseum.data.getPreviewImageUrl
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import kotlinx.serialization.Serializable

/**
 * Domain Artwork object. Nullable fields should be either null or non-blank.
 *
 * @param constituents list of constituents. The first one should contain the same data as
 * [artistRole] and [artistName] fields.
 */
@Serializable
data class Artwork(
    override val id: Int,
    val title: String,
    val constituents: List<Constituent> = emptyList(),
    val artistName: String? = null,
    val artistRole: String? = null,
    val artistBio: String? = null,
    val artistPrefix: String? = null,
    val artistSuffix: String? = null,
    val period: String? = null,
    val dynasty: String? = null,
    val reign: String? = null,
    val date: String? = null,
    val geography: String? = null,
    val culture: String? = null,
    val medium: String? = null,
    val dimensions: String? = null,
    val creditLine: String? = null,
    val classification: String? = null,
    val department: String? = null,
    val objectPageUrl: String? = null,
    val isPublicDomain: Boolean = false,
    val images: List<ArtworkImage> = emptyList(),
    val tags: List<String> = emptyList(),
) : ArtworkInfo

fun NetworkArtwork.toLocal() = Artwork(
    id = id,
    title = getTitle(),
    constituents = getConstituents(),
    artistName = artistName.takeIf { it.isNotBlank() },
    artistRole = artistRole.takeIf { it.isNotBlank() },
    artistBio = artistBio.takeIf { it.isNotBlank() },
    artistPrefix = artistPrefix.takeIf { it.isNotBlank() },
    artistSuffix = artistSuffix.takeIf { it.isNotBlank() },
    period = period.takeIf { it.isNotBlank() },
    dynasty = dynasty.takeIf { it.isNotBlank() },
    reign = reign.takeIf { it.isNotBlank() },
    date = date.takeIf { it.isNotBlank() },
    geography = getGeography(),
    culture = culture.takeIf { it.isNotBlank() },
    medium = medium.takeIf { it.isNotBlank() },
    dimensions = dimensions.takeIf { it.isNotBlank() },
    creditLine = creditLine.takeIf { it.isNotBlank() },
    classification = classification.takeIf { it.isNotBlank() },
    department = department.takeIf { it.isNotBlank() },
    images = getImages(),
    tags = getTags(),
    objectPageUrl = objectPageUrl.takeIf { it.isNotBlank() },
    isPublicDomain = isPublicDomain,
)

private fun NetworkArtwork.getTitle() =
    title.ifBlank {
        objectName
    }

private fun NetworkArtwork.getConstituents(): List<Constituent> {
    return constituents?.map {
        Constituent(it.role, it.name)
    } ?: emptyList()
}

private fun NetworkArtwork.getGeography(): String? {
    val location = buildList {
        if (city.isNotBlank()) {
            add(city)
        }
        if (state.isNotBlank()) {
            add(state)
        }
        if (county.isNotBlank()) {
            add(county)
        }
        if (country.isNotBlank()) {
            add(country)
        }
        if (region.isNotBlank()) {
            add(region)
        }
        if (subregion.isNotBlank()) {
            add(subregion)
        }
        if (locale.isNotBlank()) {
            add(locale)
        }
        if (locus.isNotBlank()) {
            add(locus)
        }
        if (excavation.isNotBlank()) {
            add(excavation)
        }
        if (river.isNotBlank()) {
            add(river)
        }
    }.joinToString(", ")
    return when {
        location.isBlank() -> null
        geographyType.isBlank() -> location
        else -> "$geographyType $location"
    }
}

private fun NetworkArtwork.getTags() =
    tags?.map { it.term } ?: emptyList()

/**
 * Convert [NetworkArtwork.primaryImageUrl] and [NetworkArtwork.additionalImagesUrls] into a
 * list of [ArtworkImage].
 * Removes duplicates.
 */
private fun NetworkArtwork.getImages(): List<ArtworkImage> {
    if (primaryImageUrl.isBlank()) {
        return emptyList()
    }

    val originals = LinkedHashSet<String>()
    originals.add(primaryImageUrl)
    additionalImagesUrls?.let {
        originals.addAll(it)
    }

    val result = mutableListOf<ArtworkImage>()
    originals.forEach { original ->
        val large = getLargeImageUrl(original)
        val preview = getPreviewImageUrl(original)
        result.add(ArtworkImage(original, large, preview))
    }
    return result
}
