package com.ashipo.metropolitanmuseum.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkConstituent(
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
