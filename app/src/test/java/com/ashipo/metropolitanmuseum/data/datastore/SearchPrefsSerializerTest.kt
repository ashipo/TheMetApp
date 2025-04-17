package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import com.ashipo.metropolitanmuseum.searchPrefs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class SearchPrefsSerializerTest {

    @Test
    fun `default departmentId is correct`() {
        assertEquals(
            -1,
            SearchPrefsSerializer.defaultValue.departmentId,
        )
    }

    @Test
    fun `default fromYear is correct`() {
        assertEquals(
            1700,
            SearchPrefsSerializer.defaultValue.fromYear,
        )
    }

    @Test
    fun `default toYear is correct`() {
        assertEquals(
            1800,
            SearchPrefsSerializer.defaultValue.toYear,
        )
    }

    @Test
    fun `reading invalid data throws CorruptionException`() = runTest {
        assertThrows<CorruptionException> {
            SearchPrefsSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
        }
    }

    @Test
    fun `writing and reading search preferences outputs correct value`() = runTest {
        val expectedUserPreferences = searchPrefs {
            byTitle = true
            byDate = true
            geoLocation.add("Moon")
            toYear = 2100
        }

        val outputStream = ByteArrayOutputStream()
        expectedUserPreferences.writeTo(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actualUserPreferences = SearchPrefsSerializer.readFrom(inputStream)

        assertEquals(
            expectedUserPreferences,
            actualUserPreferences,
        )
    }
}
