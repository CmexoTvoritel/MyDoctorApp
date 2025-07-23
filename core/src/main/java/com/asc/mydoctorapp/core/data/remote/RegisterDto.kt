package com.asc.mydoctorapp.core.data.remote

import com.asc.mydoctorapp.core.data.remote.serializers.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class RegisterDto(
    @SerialName("name") val name: String,
    @SerialName("birth") @Serializable(with = LocalDateSerializer::class) val birth: LocalDate,
    @SerialName("login") val login: String,
    @SerialName("password") val password: String
)
