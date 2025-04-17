package com.ashipo.metropolitanmuseum.data

import com.ashipo.metropolitanmuseum.Department
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.Period

interface DepartmentRepository {

    val departments: Flow<List<Department>>

    suspend fun updateDepartments(now: LocalDate, updatePeriod: Period = Period.ofDays(7))
}
