package com.ashipo.metropolitanmuseum.ui.imageviewer.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.ashipo.metropolitanmuseum.ui.model.ImageViewerParams
import kotlinx.serialization.json.Json

val ImageViewerParamsType = object : NavType<ImageViewerParams>(false) {

    override fun put(bundle: SavedState, key: String, value: ImageViewerParams) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): ImageViewerParams? {
        return Json.decodeFromString(bundle.getString(key)!!)
    }

    override fun parseValue(value: String): ImageViewerParams {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: ImageViewerParams): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
