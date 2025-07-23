package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class DoctorDto (
    val id: String?,
    val name: String?,
    val surname: String?,
    @SerializedName("father_name") val fatherName: String?,
    val email: String?,
    val speciality: String?,
    @SerializedName("working_days") val workingDays: WorkingDays?
)

data class WorkingDays (
    @SerializedName("Monday") val monday: String? = null,
    @SerializedName("Tuesday") val tuesday: String? = null,
    @SerializedName("Wednesday") val wednesday: String? = null,
    @SerializedName("Thursday") val thursday: String? = null,
    @SerializedName("Friday") val friday: String? = null,
    @SerializedName("Suturday") val saturday: String? = null,
    @SerializedName("Sunday") val sunday: String? = null,
)