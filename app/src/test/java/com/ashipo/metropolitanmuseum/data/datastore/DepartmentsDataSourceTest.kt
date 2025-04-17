package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.Departments
import com.ashipo.metropolitanmuseum.copy
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.departments
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("DepartmentsDataSource")
class DepartmentsDataSourceTest {

    private lateinit var dataStore: DataStore<Departments>
    private lateinit var subject: DepartmentsDataSource

    @BeforeEach
    fun setup() {
        dataStore = TestDataStore(departments {})
        subject = DepartmentsDataSource(dataStore)
    }

    @Test
    fun `setDepartments - updates DataStore`() = runTest {
        val expected = listOf(
            department { id = 1; name = "Dep 1" },
            department { id = 2; name = "Dep 2" },
        )

        subject.setDepartments(expected)
        val actual = dataStore.data.first().departmentsList

        assertEquals(expected, actual)
    }

    @Test
    fun `departments Flow - is backed by DataStore`() = runTest {
        val expected = listOf(
            department { id = 1; name = "Dep 1" },
            department { id = 2; name = "Dep 2" },
        )
        dataStore.updateData {
            it.copy {
                departments.clear()
                departments.addAll(expected)
            }
        }

        val actual = subject.departments.first()

        assertEquals(expected, actual)
    }
}
