package com.asc.mydoctorapp.core.data.mapper

import com.asc.mydoctorapp.core.data.remote.ChatDto
import com.asc.mydoctorapp.core.data.remote.DoctorDto
import com.asc.mydoctorapp.core.data.remote.RecordDto
import com.asc.mydoctorapp.core.data.remote.TokenDto
import com.asc.mydoctorapp.core.domain.model.ChatMessage
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.RecordInfo
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
        workingDays = workingDays
    )
}

fun RecordDto.toDomain(): RecordInfo {
    return RecordInfo(
        start = start,
        end = end,
        docName = docName,
        docSurname = docSurname,
        docSpecialty = docSpecialty,
        email = email
    )
}

fun ChatDto.toDomain(): ChatMessage {
    return ChatMessage(
        text = text,
        fromBot = true
    )
}
