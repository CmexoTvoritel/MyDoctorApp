package com.asc.mydoctorapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class RecordDto(
    val start: String?,
    val end: String?,
    @SerializedName("doc_name") val docName: String?,
    @SerializedName("doc_surname") val docSurname: String?,
    @SerializedName("doc_specialty") val docSpecialty: String?,
    @SerializedName("email") val email: String?,
)
