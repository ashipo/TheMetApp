package com.ashipo.metropolitanmuseum.ui.util

data class SharedKey(val id: Int, val type: SharedElementType, val index: Int = 0)

enum class SharedElementType {
    Image,
    Title,
    Creator,
    Period,
    Date,
    Culture,
    Medium,
}
