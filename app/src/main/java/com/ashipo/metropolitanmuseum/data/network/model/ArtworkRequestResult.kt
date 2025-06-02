package com.ashipo.metropolitanmuseum.data.network.model

sealed interface ArtworkRequestResult {

    val id: Int

    data class NotFound(override val id: Int) : ArtworkRequestResult
}
