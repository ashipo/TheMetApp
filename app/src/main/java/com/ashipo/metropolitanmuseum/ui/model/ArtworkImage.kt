package com.ashipo.metropolitanmuseum.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkImage(val originalUrl: String, val largeUrl: String, val previewUrl: String)
