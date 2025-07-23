package com.asc.mydoctorapp.core.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    @SerialName("login") val login: String,
    @SerialName("password") val password: String
)
