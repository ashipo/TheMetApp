package com.ashipo.metropolitanmuseum.data

import com.google.protobuf.Timestamp
import com.google.protobuf.timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

fun isUpdateRequired(
    lastUpdated: LocalDate?,
    now: LocalDate,
    updatePeriod: Period,
): Boolean {
    if (lastUpdated == null) return true
    return lastUpdated.plus(updatePeriod) < now
}

fun Timestamp.toLocalDate(): LocalDate =
    Instant.ofEpochSecond(seconds)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()

fun LocalDate.toTimestamp(): Timestamp {
    val epochSeconds = atStartOfDay(ZoneOffset.UTC).toEpochSecond()
    return timestamp { seconds = epochSeconds }
}

/**
 * Returns URL of the low resolution version of the original (containing "original") image URL
 *
 * @param originalUrl URL of the original image (returned in "primaryImage" and "additionalImages"
 * fields)
 */
fun getImagePreviewUrl(originalUrl: String): String =
    originalUrl.replace("original", "web-additional")
