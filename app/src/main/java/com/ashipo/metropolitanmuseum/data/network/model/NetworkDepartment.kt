package com.ashipo.metropolitanmuseum.data.network.model

import com.google.gson.annotations.SerializedName

data class NetworkDepartment(
    @SerializedName("departmentId")
    val id: Int,

    @SerializedName("displayName")
    val name: String,
)
