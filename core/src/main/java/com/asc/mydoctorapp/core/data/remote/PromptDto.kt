package com.asc.mydoctorapp.core.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromptDto(
    @SerialName("token") val token: String,
    @SerialName("prompt") val prompt: String
)
