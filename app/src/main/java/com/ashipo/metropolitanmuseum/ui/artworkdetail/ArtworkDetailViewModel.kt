package com.ashipo.metropolitanmuseum.ui.artworkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkDetailRoute
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.ArtworkType
import com.ashipo.metropolitanmuseum.ui.model.Artwork
import com.ashipo.metropolitanmuseum.ui.model.ArtworkImage
import com.ashipo.metropolitanmuseum.ui.model.Constituent
import kotlin.reflect.typeOf

class ArtworkDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artwork: Artwork = savedStateHandle.toRoute<ArtworkDetailRoute>(
        typeMap = mapOf(typeOf<Artwork>() to ArtworkType)
    ).artwork

    val uiState: ArtworkDetailScreenState = ArtworkDetailScreenState(
        id = artwork.id,
        title = artwork.title,
        constituents = artwork.constituents,
        period = artwork.period,
        dynasty = artwork.dynasty,
        reign = artwork.reign,
        date = artwork.date,
        geography = artwork.geography,
        culture = artwork.culture,
        medium = artwork.medium,
        dimensions = artwork.dimensions,
        classification = artwork.classification,
        creditLine = artwork.creditLine,
        department = artwork.department,
        images = artwork.images,
    )
}

data class ArtworkDetailScreenState(
    val id: Int,
    val title: String,
    val constituents: List<Constituent> = emptyList(),
    val period: String? = null,
    val dynasty: String? = null,
    val reign: String? = null,
    val date: String? = null,
    val geography: String? = null,
    val culture: String? = null,
    val medium: String? = null,
    val dimensions: String? = null,
    val classification: String? = null,
    val creditLine: String? = null,
    val department: String? = null,
    val images: List<ArtworkImage> = emptyList(),
)
