package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class ChatDto(
    @SerializedName("text") val text: String,
    @SerializedName("from_bot") val fromBot: Boolean
)
