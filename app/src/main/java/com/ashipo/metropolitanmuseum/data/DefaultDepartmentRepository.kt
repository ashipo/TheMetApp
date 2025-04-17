package com.ashipo.metropolitanmuseum.data

import android.util.Log
import com.ashipo.metropolitanmuseum.data.datastore.DepartmentsDataSource
import com.ashipo.metropolitanmuseum.data.datastore.LastUpdatedDataSource
import com.ashipo.metropolitanmuseum.data.network.model.mapToLocal
import com.ashipo.metropolitanmuseum.data.network.NetworkDataSource
import java.time.LocalDate
import java.time.Period
import kotlin.coroutines.cancellation.CancellationException

class DefaultDepartmentRepository(
    private val departmentsDataSource: DepartmentsDataSource,
    private val lastUpdatedDataSource: LastUpdatedDataSource,
    private val network: NetworkDataSource,
) : DepartmentRepository {

    override val departments = departmentsDataSource.departments

    override suspend fun updateDepartments(now: LocalDate, updatePeriod: Period) {
        val lastUpdated = lastUpdatedDataSource.getDepartmentsLastUpdate()
        if (isUpdateRequired(lastUpdated, now, updatePeriod)) {
            try {
                val departments = network.getDepartments().mapToLocal()
                departmentsDataSource.setDepartments(departments)
                lastUpdatedDataSource.setDepartmentsLastUpdate(now)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.e(TAG, "Error fetching departments", exception)
            }
        }
    }

    companion object {
        const val TAG = "DefaultDepartmentRepository"
    }
}
