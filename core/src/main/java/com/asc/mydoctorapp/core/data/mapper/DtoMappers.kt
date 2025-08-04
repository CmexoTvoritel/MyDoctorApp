package com.asc.mydoctorapp.core.data.mapper

import com.asc.mydoctorapp.core.data.remote.ChatDto
import com.asc.mydoctorapp.core.data.remote.ClinicInfoDto
import com.asc.mydoctorapp.core.data.remote.DoctorDto
import com.asc.mydoctorapp.core.data.remote.RecordDto
import com.asc.mydoctorapp.core.data.remote.TokenDto
import com.asc.mydoctorapp.core.data.remote.UserDto
import com.asc.mydoctorapp.core.data.remote.WorkingDays
import com.asc.mydoctorapp.core.domain.model.ChatMessage
import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.core.domain.model.Doctor
import com.asc.mydoctorapp.core.domain.model.RecordInfo
import com.asc.mydoctorapp.core.domain.model.UserInfo
import com.asc.mydoctorapp.core.domain.model.UserToken

fun TokenDto.toDomain(): UserToken {
    return UserToken(value = token)
}

fun DoctorDto.toDomain(clinicName: String): Doctor {
    return Doctor(
        name = name ?: "",
        surname = surname ?: "",
        specialty = speciality ?: "Врач",
        email = email ?: "",
        clinic = clinicName,
        workingDays = workingDays
    )
}

fun RecordDto.toDomain(isConfirmed: Boolean): RecordInfo {
    return RecordInfo(
        start = start,
        end = end,
        docName = docName,
        docSurname = docSurname,
        docSpecialty = docSpecialty,
        email = email,
        clinicName = clinicName,
        isConfirmed = isConfirmed
    )
}

fun ChatDto.toDomain(): ChatMessage {
    return ChatMessage(
        text = text,
        fromBot = true
    )
}

fun UserDto.toDomain(): UserInfo {
    return UserInfo(
        name = name ?: "",
        login = login ?: "",
        birth = birth ?: "",
    )
}

fun ClinicInfoDto.toDomain(): Clinic {
    return Clinic(
        name = name ?: "",
        address = address ?: "",
        email = email ?: "",
        phone = phone ?: "",
        workingDays = workingDays?.toRuString()
    )
}

private val dayRu = mapOf(
    "Monday" to "пн",
    "Tuesday" to "вт",
    "Wednesday" to "ср",
    "Thursday" to "чт",
    "Friday" to "пт",
    "Suturday" to "сб",
    "Sunday" to "вс"
)

private fun WorkingDays.toRuString(): String {
    val parts = buildList {
        monday?.let { add("${dayRu["Monday"]}: $it") }
        tuesday?.let { add("${dayRu["Tuesday"]}: $it") }
        wednesday?.let { add("${dayRu["Wednesday"]}: $it") }
        thursday?.let { add("${dayRu["Thursday"]}: $it") }
        friday?.let { add("${dayRu["Friday"]}: $it") }
        saturday?.let { add("${dayRu["Suturday"]}: $it") }
        sunday?.let { add("${dayRu["Sunday"]}: $it") }
    }
    return parts.joinToString(separator = ", ")
}
