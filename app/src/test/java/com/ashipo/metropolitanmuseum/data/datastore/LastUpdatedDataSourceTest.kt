package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.LastUpdated
import com.ashipo.metropolitanmuseum.copy
import com.ashipo.metropolitanmuseum.data.toLocalDate
import com.ashipo.metropolitanmuseum.data.toTimestamp
import com.ashipo.metropolitanmuseum.lastUpdated
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

@DisplayName("LastUpdatedDataSource")
class LastUpdatedDataSourceTest {

    private lateinit var dataStore: DataStore<LastUpdated>
    private lateinit var subject: LastUpdatedDataSource

    @BeforeEach
    fun setup() {
        dataStore = TestDataStore(lastUpdated {})
        subject = LastUpdatedDataSource(dataStore)
    }

    @Test
    fun `setDepartmentsLastUpdate - updates DataStore`() = runTest {
        val expected = LocalDate.now()

        subject.setDepartmentsLastUpdate(expected)
        val actual = dataStore.data.first().departments.toLocalDate()

        assertEquals(expected, actual)
    }

    @Test
    fun `getDepartmentsLastUpdate - is backed by DataStore`() = runTest {
        val expected = LocalDate.now()
        dataStore.updateData {
            it.copy {
                departments = expected.toTimestamp()
            }
        }

        val actual = subject.getDepartmentsLastUpdate()

        assertEquals(expected, actual)
    }
}
