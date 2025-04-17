package com.ashipo.metropolitanmuseum.sync

import com.ashipo.metropolitanmuseum.data.DepartmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

object Sync : KoinComponent {
    private val departmentRepository: DepartmentRepository by inject()
    private val scope = CoroutineScope(SupervisorJob())

    fun start() = scope.launch {
        departmentRepository.updateDepartments(LocalDate.now())
    }
}
