package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class ClinicInfoDto (
    val name: String?,
    val address: String?,
    val email: String?,
    @SerializedName("phone_number") val phone: String?,
    @SerializedName("working_days") val workingDays: WorkingDays?
)