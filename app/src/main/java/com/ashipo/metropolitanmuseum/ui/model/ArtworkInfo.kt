package com.ashipo.metropolitanmuseum.ui.model

import com.ashipo.metropolitanmuseum.data.network.model.ArtworkRequestResult

sealed interface ArtworkInfo {

    val id: Int

    data class NotFound(override val id: Int) : ArtworkInfo
}

fun ArtworkRequestResult.NotFound.toLocal() =
    ArtworkInfo.NotFound(id)
