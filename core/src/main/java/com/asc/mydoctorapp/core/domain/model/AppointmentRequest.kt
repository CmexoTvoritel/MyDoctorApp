package com.asc.mydoctorapp.core.domain.model

data class AppointmentRequest(
    val doctorEmail: String,
    val token: UserToken,
    val day: String,
    val month: String,
    val year: String,
    val hour: String,
    val minutes: String
)
