package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("access_token") val accessToken: String?,
    val error: String
)