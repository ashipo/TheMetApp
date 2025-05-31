package com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.ashipo.metropolitanmuseum.data.network.model.NetworkArtwork
import kotlinx.serialization.json.Json

val ArtworkType = object : NavType<NetworkArtwork>(false) {

    override fun put(bundle: SavedState, key: String, value: NetworkArtwork) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): NetworkArtwork? {
        return Json.decodeFromString(bundle.getString(key)!!)
    }

    override fun parseValue(value: String): NetworkArtwork {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: NetworkArtwork): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
