package com.ashipo.metropolitanmuseum.data.network.model

import com.ashipo.metropolitanmuseum.Department
import com.ashipo.metropolitanmuseum.department

data class NetworkDepartments(val departments: List<NetworkDepartment>)

fun NetworkDepartments.mapToLocal(): List<Department> = departments.map {
    department {
        id = it.id
        name = it.name
    }
}
