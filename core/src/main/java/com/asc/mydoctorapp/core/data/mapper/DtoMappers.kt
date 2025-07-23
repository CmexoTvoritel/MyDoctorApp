package com.asc.mydoctorapp.core.data.mapper

import com.asc.mydoctorapp.core.data.remote.AppointmentDto
import com.asc.mydoctorapp.core.data.remote.ChatDto
import com.asc.mydoctorapp.core.data.remote.DoctorDto
import com.asc.mydoctorapp.core.data.remote.TokenDto
import com.asc.mydoctorapp.core.domain.model.AppointmentRequest
import com.asc.mydoctorapp.core.domain.model.ChatMessage
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.UserToken

fun TokenDto.toDomain(): UserToken {
    return UserToken(value = token)
}

fun DoctorDto.toDomain(): Doctor {
    return Doctor(
        name = name ?: "",
        surname = surname ?: "",
        specialty = speciality ?: "Врач",
        email = email ?: "",
        clinic = "Clinic1",
    )
}

fun ChatDto.toDomain(): ChatMessage {
    return ChatMessage(
        text = text,
        fromBot = fromBot
    )
}

fun AppointmentRequest.toDto(): AppointmentDto {
    return AppointmentDto(
        doctorEmail = doctorEmail,
        token = token.value,
        dateTime = dateTime
    )
}
