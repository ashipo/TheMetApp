package com.ashipo.metropolitanmuseum.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * @param id Identifying number for each artwork (unique, can be used as key field)
 * @param isHighlight Indicates a popular and important artwork in the collection
 * @param isPublicDomain Indicates an artwork in the Public Domain
 * @param department Indicates The Met's curatorial department responsible for the artwork
 * @param objectName Describes the physical type of the object ("Dress", "Painting", "Photograph")
 * @param title Title, identifying phrase, or name given to a work of art
 * @param culture Information about the culture, or people from which an object was created
 * @param period Time or time period when an object was created
 * @param artistName Artist name in the correct order for display
 * @param date Describes the specific or approximate date when an artwork was designed or created
 * @param medium Materials that were used to create the artwork ("Oil on canvas", "Watercolor",
 * "Gold")
 * @param classification Artwork type ("Basketry", "Ceramics", "Paintings")
 * @param tags An array of subject keyword tags associated with the object and their respective
 * AAT URL
 * @param geographyType Qualifying information that describes the relationship of the place
 * catalogued in the geography fields to the object that is being catalogued ("Made in", "From",
 * "Attributed to")
 * @param primaryImagePreviewUrl Primary image preview URL
 * @param primaryImageUrl Primary image URL
 * @param additionalImagesUrls URLs to the additional images
 * @param objectPageUrl URL to object's page on metmuseum.org
 */
@Serializable
data class Artwork(
    @SerializedName("objectID")
    override val id: Int,
    val isHighlight: Boolean = false,
    val isPublicDomain: Boolean = false,
    val department: String = "",
    val objectName: String = "",
    val title: String = "",
    val culture: String = "",
    val period: String = "",
    val constituents: List<Constituent>? = null,
    val artistRole: String = "",
    val artistPrefix: String = "",
    @SerializedName("artistDisplayName")
    val artistName: String = "",
    @SerializedName("artistDisplayBio")
    val artistBio: String = "",
    val artistSuffix: String = "",
    @SerializedName("objectDate")
    val date: String = "",
    val medium: String = "",
    val classification: String = "",
    val tags: List<Tag>? = null,

    val geographyType: String = "",
    val city: String = "",
    val state: String = "",
    val county: String = "",
    val country: String = "",
    val region: String = "",
    val subregion: String = "",
    val locale: String = "",
    val locus: String = "",
    val excavation: String = "",
    val river: String = "",

    @SerializedName("primaryImageSmall")
    val primaryImagePreviewUrl: String = "",
    @SerializedName("primaryImage")
    val primaryImageUrl: String = "",
    @SerializedName("additionalImages")
    val additionalImagesUrls: List<String>? = null,
    @SerializedName("objectURL")
    val objectPageUrl: String = "",
) : ArtworkResult

sealed interface ArtworkResult {

    val id: Int

    data class NotFound(override val id: Int) : ArtworkResult
}

@Serializable
data class Tag(
    val term: String,
    @SerializedName("AAT_URL")
    val aatUrl: String,
    @SerializedName("Wikidata_URL")
    val wikidataUrl: String,
)

@Serializable
data class Constituent(
    @SerializedName("constituentID")
    val id: Int,
    val role: String,
    val name: String,
    @SerializedName("constituentULAN_URL")
    val ulanUrl: String,
    @SerializedName("constituentWikidata_URL")
    val wikidataUrl: String,
    val gender: String,
)
