package com.asc.mydoctorapp.core.data.remote

import com.asc.mydoctorapp.core.data.remote.serializers.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AppointmentDto(
    @SerialName("doctor_email") val doctorEmail: String,
    @SerialName("token") val token: String,
    @SerialName("date_time") @Serializable(with = LocalDateTimeSerializer::class) val dateTime: LocalDateTime
)
