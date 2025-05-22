package com.ashipo.metropolitanmuseum.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtworkImage(val originalUrl: String, val largeUrl: String, val previewUrl: String) :
    Parcelable
