package com.ashipo.metropolitanmuseum.ui.artwork.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import kotlinx.serialization.json.Json

val ArtworkType = object : NavType<Artwork>(false) {

    override fun put(bundle: SavedState, key: String, value: Artwork) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): Artwork? {
        return Json.decodeFromString(bundle.getString(key)!!)
    }

    override fun parseValue(value: String): Artwork {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Artwork): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
