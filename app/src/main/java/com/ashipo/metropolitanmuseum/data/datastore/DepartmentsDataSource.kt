package com.ashipo.metropolitanmuseum.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.Department
import com.ashipo.metropolitanmuseum.Departments
import com.ashipo.metropolitanmuseum.departments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class DepartmentsDataSource(
    private val dataStore: DataStore<Departments>,
) {
    val departments: Flow<List<Department>> = dataStore.data.map { it.departmentsList }

    suspend fun setDepartments(newDepartments: List<Department>) {
        try {
            dataStore.updateData {
                departments { departments.addAll(newDepartments) }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update departments", e)
        }
    }

    companion object {
        const val TAG = "DepartmentsDataSource"
    }
}