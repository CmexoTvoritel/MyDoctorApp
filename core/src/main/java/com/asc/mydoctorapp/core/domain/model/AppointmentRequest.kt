package com.asc.mydoctorapp.core.domain.model

import java.time.LocalDateTime

data class AppointmentRequest(
    val doctorEmail: String,
    val token: UserToken,
    val dateTime: LocalDateTime
)
