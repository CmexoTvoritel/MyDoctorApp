package com.asc.mydoctorapp.core.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicDto(
    @SerialName("clinic_name") val clinicName: String
)
