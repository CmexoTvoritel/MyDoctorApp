package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class ChatDto(
    @SerializedName("response") val text: String
)
