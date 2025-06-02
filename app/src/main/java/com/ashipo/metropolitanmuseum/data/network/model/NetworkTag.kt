package com.ashipo.metropolitanmuseum.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTag(
    val term: String,
    @SerializedName("AAT_URL")
    val aatUrl: String,
    @SerializedName("Wikidata_URL")
    val wikidataUrl: String,
)
