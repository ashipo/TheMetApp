package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import com.ashipo.metropolitanmuseum.data.toTimestamp
import com.ashipo.metropolitanmuseum.lastUpdated
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlin.test.assertEquals

class LastUpdatedSerializerTest {

    @Test
    fun `default lastUpdated is empty`() {
        assertEquals(
            lastUpdated {},
            LastUpdatedSerializer.defaultValue,
        )
    }

    @Test
    fun `reading invalid data throws CorruptionException`() = runTest {
        assertThrows<CorruptionException> {
            LastUpdatedSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
        }
    }

    @Test
    fun `writing and reading last updated outputs correct value`() = runTest {
        val expectedLastUpdated = lastUpdated {
            departments = LocalDate.now().toTimestamp()
        }

        val outputStream = ByteArrayOutputStream()
        expectedLastUpdated.writeTo(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actualLastUpdated = LastUpdatedSerializer.readFrom(inputStream)

        assertEquals(
            expectedLastUpdated,
            actualLastUpdated,
        )
    }
}