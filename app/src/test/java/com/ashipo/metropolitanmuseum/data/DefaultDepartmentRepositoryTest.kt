package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.data.datastore.DepartmentsDataSource
import com.ashipo.metropolitanmuseum.data.datastore.LastUpdatedDataSource
import com.ashipo.metropolitanmuseum.data.datastore.TestDataStore
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartment
import com.ashipo.metropolitanmuseum.data.network.model.NetworkDepartments
import com.ashipo.metropolitanmuseum.data.network.model.mapToLocal
import com.ashipo.metropolitanmuseum.data.network.TestNetworkDataSource
import com.ashipo.metropolitanmuseum.departments
import com.ashipo.metropolitanmuseum.lastUpdated
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period
import kotlin.test.assertEquals

@DisplayName("DefaultDepartmentRepository")
class DefaultDepartmentRepositoryTest {

    private lateinit var departmentsDataSource: DepartmentsDataSource
    private lateinit var lastUpdatedDataSource: LastUpdatedDataSource
    private lateinit var network: TestNetworkDataSource
    private lateinit var subject: DefaultDepartmentRepository

    @BeforeEach
    fun setup() {
        departmentsDataSource = DepartmentsDataSource(TestDataStore(departments {}))
        lastUpdatedDataSource = LastUpdatedDataSource(TestDataStore(lastUpdated {}))
        network = TestNetworkDataSource()
        subject = DefaultDepartmentRepository(departmentsDataSource, lastUpdatedDataSource, network)
    }

    @Test
    fun `updateDepartments - when update is needed - updates 'LastUpdated'`() = runTest {
        val expected = LocalDate.of(2000, 1, 10)

        subject.updateDepartments(expected, Period.ofDays(7))
        val actual = lastUpdatedDataSource.getDepartmentsLastUpdate()

        assertEquals(expected, actual)
    }

    @Test
    fun `updateDepartments - when update is needed - pulls departments from network`() = runTest {
        val networkDepartments = NetworkDepartments(
            listOf(
                NetworkDepartment(1, "Uno"),
                NetworkDepartment(2, "Dos"),
            )
        )
        network.setDepartments(networkDepartments)
        val expected = networkDepartments.mapToLocal()

        subject.updateDepartments(LocalDate.now())
        val actual = departmentsDataSource.departments.first()

        assertEquals(expected, actual)
    }
}
