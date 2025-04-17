package com.ashipo.metropolitanmuseum.data.datastore

import com.ashipo.metropolitanmuseum.Department
import com.ashipo.metropolitanmuseum.data.DepartmentRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate
import java.time.Period

class TestDepartmentRepository : DepartmentRepository {

    private val _departments: MutableSharedFlow<List<Department>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val departments: Flow<List<Department>> = _departments

    override suspend fun updateDepartments(now: LocalDate, updatePeriod: Period) {}

    /**
     * Test only
     */
    fun setDepartments(departments: List<Department>) {
        _departments.tryEmit(departments)
    }
}
