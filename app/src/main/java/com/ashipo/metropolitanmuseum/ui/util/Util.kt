package com.ashipo.metropolitanmuseum.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
import androidx.core.net.toUri

const val SEARCH_MAX_LENGTH = 250
const val MEDIUM_MAX_LENGTH = 30
const val MEDIUM_MAX_COUNT = 5
const val GEO_LOCATION_MAX_LENGTH = 50
const val GEO_LOCATION_MAX_COUNT = 5

fun openUrl(url: String, context: Context) {
    val webpage: Uri = url.toUri()
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun buildDescriptionString(
    label: String,
    value: String,
    convertHtml: Boolean = true,
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(label)
            append(": ")
        }
        if (convertHtml) {
            append(AnnotatedString.fromHtml(value))
        } else {
            append(value)
        }
    }
}
