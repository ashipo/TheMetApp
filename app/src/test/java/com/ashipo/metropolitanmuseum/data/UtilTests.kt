package com.ashipo.metropolitanmuseum.data

import com.google.protobuf.timestamp
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.Period
import java.util.stream.Stream
import kotlin.test.assertEquals

class UtilTests {

    @ParameterizedTest(name = "[Last update: {0}, Now: {1}, Period: {2}, result: {3}]")
    @MethodSource("com.ashipo.metropolitanmuseum.data.UtilTests#updateRequiredProvider")
    fun `isUpdateRequired - returns correct value`(
        lastUpdated: LocalDate?,
        now: LocalDate,
        updatePeriod: Period,
        expected: Boolean,
    ) {
        assertEquals(expected, isUpdateRequired(lastUpdated, now, updatePeriod))
    }

    @Test
    fun `Timestamp_toLocalDate - converts correctly`() {
        val expectedDate = LocalDate.of(2025, 3, 25)
        val timestamp = timestamp { seconds = 1742893936 }

        val actualDate = timestamp.toLocalDate()

        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun `LocalDate_toTimestamp - converts correctly`() {
        val expectedTimestamp = timestamp { seconds = 1742860800 }
        val date = LocalDate.of(2025, 3, 25)

        val actualTimestamp = date.toTimestamp()

        assertEquals(expectedTimestamp, actualTimestamp)
    }

    companion object {

        // Last update, now, period, expected result
        @JvmStatic
        fun updateRequiredProvider(): Stream<Arguments> = Stream.of(
            arguments(null, LocalDate.of(2020, 1, 1), Period.ofDays(7), true),
            arguments(LocalDate.ofEpochDay(0), LocalDate.of(2020, 6, 1), Period.ofDays(7), true),
            arguments(LocalDate.ofEpochDay(0), LocalDate.of(2020, 5, 30), Period.ofMonths(9), true),
            arguments(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 30), Period.ofDays(7), true),
            arguments(LocalDate.of(2020, 5, 30), LocalDate.of(2020, 6, 1), Period.ofDays(7), false),
        )
    }
}
