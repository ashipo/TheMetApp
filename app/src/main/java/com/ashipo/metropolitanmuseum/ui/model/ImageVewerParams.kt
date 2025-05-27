package com.ashipo.metropolitanmuseum.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageViewerParams(val images: List<ArtworkImage>, val initialImageIndex: Int)
