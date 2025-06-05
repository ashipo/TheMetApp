package com.ashipo.metropolitanmuseum.ui.model

import kotlinx.serialization.Serializable

/**
 * @param role constituent's role
 * @param name may contain HTML tags
 */
@Serializable
data class Constituent(val role: String, val name: String)
